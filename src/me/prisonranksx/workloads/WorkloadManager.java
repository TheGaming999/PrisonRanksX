package me.prisonranksx.workloads;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.collect.ConcurrentHashMultiset;

import me.prisonranksx.PrisonRanksX;
import me.prisonranksx.api.PRXAPI;
import me.prisonranksx.data.IPrestigeDataHandler;
import me.prisonranksx.data.InfinitePrestigeSettings;
import me.prisonranksx.data.RankPath;
import me.prisonranksx.events.RankUpdateCause;
import me.prisonranksx.events.RankUpdateEvent;
import me.prisonranksx.hooks.PapiHook;
import me.prisonranksx.utils.XUUID;

public class WorkloadManager {

	private static final Map<UUID, WorkloadRunnable> WORKLOADS = new HashMap<>();

	private static final MainGetter MAIN_GETTER = new MainGetter();
	private static final PrisonRanksX MAIN = MAIN_GETTER.get();

	@SuppressWarnings("unused")
	private static class MainGetter {

		private PrisonRanksX main;
		private PRXAPI api;
		private Set<String> processingPlayers;
		private List<String> lastPrestigeMessage;
		private List<String> notEnoughMoneyMessage;
		private String finalInfinitePrestige;
		private String noPrestigeMessage;
		private ConcurrentHashMultiset<String> multiThreadSet;
		private int prestigesPerTick = 5;
		private int threadTimer = 1;
		private Map<String, Map<String, Double>> chancesCache;
		private Set<String> stopSignal;
		private String prestigeMaxMessage;
		private boolean isLastPrestigeMsgOnly;
		private boolean prestigeMessageHasValue;
		private String prestigeMessage;

		public PRXAPI getAPI() {
			return api;
		}

		public String getFinalInfinitePrestige() {
			return finalInfinitePrestige;
		}

		private MainGetter() {
			reload();
		}

		private PrisonRanksX get() {
			return main;
		}

		private PrisonRanksX reload() {
			main = (PrisonRanksX) JavaPlugin.getProvidingPlugin(WorkloadManager.class);
			api = main.prxAPI;
			processingPlayers = new HashSet<>();
			lastPrestigeMessage = getAPI().cl(getAPI().h("lastprestige"));
			notEnoughMoneyMessage = getAPI().cl(getAPI().h("prestige-notenoughmoney"));
			noPrestigeMessage = getAPI().g("noprestige");
			multiThreadSet = ConcurrentHashMultiset.create();
			chancesCache = new HashMap<>();
			prestigesPerTick = 5;
			threadTimer = 1;
			finalInfinitePrestige = main.isInfinitePrestige
					? String.valueOf(main.infinitePrestigeSettings.getFinalPrestige())
					: String.valueOf(getAPI().getLastPrestige());
			stopSignal = new HashSet<>();
			isLastPrestigeMsgOnly = main.getGlobalStorage()
					.getBooleanData("Options.prestigemax-prestigemsglastprestigeonly");
			prestigeMaxMessage = getAPI().g("prestigemax");
			prestigeMessage = main.messagesStorage.getStringMessage("prestige");
			prestigeMessageHasValue = (prestigeMessage != null && !prestigeMessage.isEmpty());
			return main;
		}

	}

	private static interface Workload {

		boolean compute();

		String toString();

	}

	public static PRXAPI getAPI() {
		return MAIN.prxAPI;
	}

	public static WorkloadRunnable create(UUID uniqueId, WorkloadRunnable runnable) {
		if(WORKLOADS.containsKey(uniqueId)) WORKLOADS.remove(uniqueId);
		WORKLOADS.put(uniqueId, runnable);
		return WORKLOADS.get(uniqueId);
	}

	public static WorkloadRunnable get(UUID uniqueId) {
		return WORKLOADS.get(uniqueId);
	}

	public static void addWorkload(UUID uniqueId, Workload workload) {
		WORKLOADS.get(uniqueId).addWorkload(workload);
	}

	public static void clearWorkloads() {
		WORKLOADS.clear();
	}

	public static class WorkloadRunnable implements Runnable {

		private static final double MAX_MILLIS_PER_TICK = 2.0;
		private static final int MAX_NANOS_PER_TICK = (int) (MAX_MILLIS_PER_TICK * 1E6);

		private final Deque<Workload> workloadDeque;	
		private WhenCompleteWorkload whenComplete;

		public static void refreshValues() {
			MAIN_GETTER.reload();
		}

		private UUID uniqueId;

		public WorkloadRunnable(UUID uniqueId) {
			this.uniqueId = uniqueId;
			workloadDeque = new ArrayDeque<>();
			refreshValues();
		}

		public UUID getUniqueId() {
			return this.uniqueId;
		}

		public void addWorkload(Workload workload) {
			this.workloadDeque.add(workload);
		}

		public void whenComplete(Runnable runnable) {
			whenComplete = new WhenCompleteWorkload(runnable);
		}

		public void clearWorkloads() {
			workloadDeque.clear();
		}

		@Override
		public void run() {
			long stopTime = System.nanoTime() + MAX_NANOS_PER_TICK;

			Workload nextLoad;

			while (System.nanoTime() <= stopTime && (nextLoad = this.workloadDeque.poll()) != null) {
				if(MAIN.getPrestigeMax().hasStopSignal(nextLoad.toString())) {
					whenComplete.compute();
					MAIN_GETTER.get().debug("When complete computed.");
					clearWorkloads();
					break;
				}
				nextLoad.compute();
			}
		}

	}

	public static class PrestigeWorkload implements Workload {

		private Player p;
		private UUID uuid;
		private String name;
		private final PrisonRanksX plugin = MAIN;
		private int i;
		private String rebirthName;
		private static final Map<UUID, Double> TAKEN_BALANCE = new HashMap<>();
		private static final Map<UUID, Integer> PRESTIGE_TIMES = new HashMap<>();
		private static final Map<UUID, Double> VIRTUAL_BALANCE = new HashMap<>();
		private static final Map<UUID, String> FINAL_PRESTIGE = new HashMap<>();

		public static void clear(UUID uuid) {
			TAKEN_BALANCE.put(uuid, 0.0d);
			PRESTIGE_TIMES.put(uuid, 0);
			VIRTUAL_BALANCE.put(uuid, getAPI().getPlayerMoney(XUUID.getNameFromUUID(uuid)));
			FINAL_PRESTIGE.remove(uuid);
		}

		public PrestigeWorkload(Player player, UUID uuid, String name, int prestigeNumber, String rebirthName) {
			this.p = player;
			this.uuid = uuid;
			this.name = name;
			this.i = prestigeNumber;
			this.rebirthName = rebirthName;
		}

		@Override
		public String toString() {
			return name;
		}

		public static double getTakenBalance(UUID uuid) {
			return TAKEN_BALANCE.get(uuid);
		}

		public static int getPrestigeStreak(UUID uuid) {
			return PRESTIGE_TIMES.get(uuid);
		}

		public static double getVirtualBalance(UUID uuid) {
			return VIRTUAL_BALANCE.get(uuid);
		}

		public static String getFinalPrestige(UUID uuid) {
			return FINAL_PRESTIGE.get(uuid);
		}

		public static double setVirtualBalance(UUID uuid, double amount) {
			return VIRTUAL_BALANCE.put(uuid, amount);
		}

		private boolean isOnline(Player p) {
			return p != null && p.isOnline();
		}

		private boolean isPrestigeMessageNull() {
			return !MAIN_GETTER.prestigeMessageHasValue;
		}

		private boolean isNullOrEmpty(List<String> stringList) {
			return stringList == null || stringList.isEmpty();
		}

		private boolean hasStopSignal(String name) {
			return MAIN.getPrestigeMax().hasStopSignal(name);
		}

		private boolean sendStopSignal(String name) {
			return MAIN.getPrestigeMax().sendStopSignal(name);
		}

		@Override
		public boolean compute() {
			RankPath loopRankPath = getAPI().getPlayerRankPath(uuid);
			if(!getAPI().isLastRank(loopRankPath) && !getAPI().hasAllowPrestige(loopRankPath)) {
				if (getAPI().canRankup(p))
					getAPI().rankupMax(p);
				return sendStopSignal(name);
			}
			if(!plugin.getPlayerStorage().getPlayerData().containsKey(uuid.toString())) {
				MAIN.getPrestigeMax().removeProcessingPlayer(name);
				return sendStopSignal(name);
			}
			String loopPrestigeName = String.valueOf(i);
			double loopBalance = VIRTUAL_BALANCE.get(uuid);
			IPrestigeDataHandler loopPrestige = getAPI().getPrestige(loopPrestigeName);
			String loopNextPrestigeName = loopPrestige.getNextPrestigeName();
			if (loopPrestigeName.equals(MAIN_GETTER.getFinalInfinitePrestige())) {
				if(!isOnline(p)) return sendStopSignal(name);
				MAIN_GETTER.lastPrestigeMessage.forEach(p::sendMessage);
				plugin.debug("Last prestige reached");
				return sendStopSignal(name);
			}
			IPrestigeDataHandler loopNextPrestige = getAPI().getPrestige(loopNextPrestigeName);
			double loopNextPrestigeCost = getAPI().getIncreasedPrestigeCost(rebirthName, loopNextPrestigeName);		
			String loopNextPrestigeCostFormatted = getAPI().formatBalance(loopNextPrestigeCost);
			String loopNextPrestigeDisplay = getAPI().c(loopNextPrestige.getDisplayName());
			if(loopNextPrestigeCost > loopBalance) {
				if(!isOnline(p)) return sendStopSignal(name);
				MAIN_GETTER.notEnoughMoneyMessage.forEach(messageLine -> {
					p.sendMessage(plugin.getString(messageLine, name)
							.replace("%player%", name)
							.replace("%nextprestige%", loopNextPrestigeName)
							.replace("%nextprestige_display%", loopNextPrestigeDisplay)
							.replace("%nextprestige_cost%", String.valueOf(loopNextPrestigeCost))
							.replace("%prestige_usformat%", PapiHook.nf.format(Double.valueOf(loopPrestigeName)))
							.replace("%nextprestige_usformat%", PapiHook.nf.format(Double.valueOf(loopNextPrestigeName)))
							.replace("%nextprestige_cost_formatted%", loopNextPrestigeCostFormatted));
				});
				return sendStopSignal(name);
			}
			TAKEN_BALANCE.put(uuid, TAKEN_BALANCE.get(uuid) + loopNextPrestigeCost);
			VIRTUAL_BALANCE.put(uuid, VIRTUAL_BALANCE.get(uuid) - loopNextPrestigeCost);
			if (!isPrestigeMessageNull() && !MAIN_GETTER.isLastPrestigeMsgOnly) {
				if(isOnline(p) && !hasStopSignal(name))
					p.sendMessage(MAIN_GETTER.prestigeMessage
							.replace("%prestige_usformat%", PapiHook.nf.format(Double.valueOf(loopPrestigeName)))
							.replace("%nextprestige_usformat%", PapiHook.nf.format(Double.valueOf(loopNextPrestigeName)))
							.replace("%nextprestige%", loopNextPrestigeName)
							.replace("%nextprestige_display%", loopNextPrestigeDisplay)
							);	
			}
			List<String> loopNextPrestigeCommands = loopNextPrestige.getPrestigeCommands();
			if(!isNullOrEmpty(loopNextPrestigeCommands)) {
				if(isOnline(p) && !hasStopSignal(name))
					plugin.getServer().getScheduler().runTask(plugin, () -> {
						plugin.executeCommands(p, loopNextPrestigeCommands);
					});
			}
			List<String> loopNextPrestigeBroadcast = loopNextPrestige.getBroadcast();
			if(!isNullOrEmpty(loopNextPrestigeBroadcast)) {
				loopNextPrestigeBroadcast.forEach(broadcastMessage -> {
					Bukkit.broadcastMessage(plugin.getString(broadcastMessage, name)
							.replace("%player%", name)
							.replace("%prestige%", loopPrestigeName)
							.replace("%prestige_usformat%", PapiHook.nf.format(Double.valueOf(loopPrestigeName)))
							.replace("%nextprestige_usformat%", PapiHook.nf.format(Double.valueOf(loopNextPrestigeName)))
							.replace("%nextprestige%", loopNextPrestigeName)
							.replace("%nextprestige_display%", loopNextPrestigeDisplay)
							);
				});
			}
			List<String> loopNextPrestigeMessage = loopNextPrestige.getMsg();
			if(!isNullOrEmpty(loopNextPrestigeMessage)) {
				if(!isOnline(p) || hasStopSignal(name)) return sendStopSignal(name);
				loopNextPrestigeMessage.forEach(messageLine -> {
					p.sendMessage(plugin.getString(messageLine, name)
							.replace("%player%", name)
							.replace("%prestige%", loopPrestigeName)
							.replace("%prestige_usformat%", PapiHook.nf.format(Double.valueOf(loopPrestigeName)))
							.replace("%nextprestige_usformat%", PapiHook.nf.format(Double.valueOf(loopNextPrestigeName)))
							.replace("%nextprestige%", loopNextPrestigeName)
							.replace("%nextprestige_display%", loopNextPrestigeDisplay)
							);
				});
			}
			List<String> commands = plugin.infinitePrestigeSettings.getCommands();
			if(commands != null && !commands.isEmpty()) {
				plugin.getServer().getScheduler().runTask(plugin, () -> {
					commands.forEach(cmd -> {
						plugin.executeCommand(p, plugin.getString(cmd
								.replace("{number}", loopPrestigeName)));
					});
				});
			}
			List<String> broadcast = plugin.infinitePrestigeSettings.getBroadcast();
			if(broadcast != null && !broadcast.isEmpty()) {
				broadcast.forEach(broadcastMessage -> {
					Bukkit.broadcastMessage(plugin.getString(broadcastMessage.replace("{number}", loopPrestigeName), name)
							.replace("%player%", name)
							.replace("%prestige%", loopPrestigeName)
							.replace("%nextprestige%", loopNextPrestigeName)
							.replace("%nextprestige_display%", loopNextPrestigeDisplay)
							);
				});
			}
			Map<Long, InfinitePrestigeSettings> continuousPrestigeSettings = plugin.infinitePrestigeSettings.getContinuousPrestigeSettings();
			if(!continuousPrestigeSettings.isEmpty()) {
				if(!loopNextPrestigeName.equals("0") && !loopNextPrestigeName.equals("1")) {
					continuousPrestigeSettings.entrySet().forEach(settingEntry -> {
						if(!getAPI().getNumberAPI().hasUsableDecimals((double)Long.valueOf(loopNextPrestigeName) / (double)settingEntry.getKey())) {
							List<String> continuousBroadcast = settingEntry.getValue().getBroadcast();
							if(!isNullOrEmpty(continuousBroadcast)) {
								continuousBroadcast.forEach(broadcastMessage -> {
									Bukkit.broadcastMessage(plugin.getString(broadcastMessage.replace("{number}", loopNextPrestigeName), name)
											.replace("%player%", name)
											.replace("%prestige%", loopPrestigeName)
											.replace("%prestige_usformat%", PapiHook.nf.format(Double.valueOf(loopPrestigeName)))
											.replace("%nextprestige_usformat%", PapiHook.nf.format(Double.valueOf(loopNextPrestigeName)))
											.replace("%nextprestige%", loopNextPrestigeName)
											.replace("%nextprestige_display%", loopNextPrestigeDisplay)
											);
								});
							}
							List<String> continuousMessage = settingEntry.getValue().getMsg();
							if(!isNullOrEmpty(continuousMessage)) {
								continuousMessage.forEach(message -> {
									p.sendMessage(plugin.getString(message.replace("{number}", loopNextPrestigeName), name)
											.replace("%player%", name)
											.replace("%prestige%", loopPrestigeName)
											.replace("%prestige_usformat%", PapiHook.nf.format(Double.valueOf(loopPrestigeName)))
											.replace("%nextprestige_usformat%", PapiHook.nf.format(Double.valueOf(loopNextPrestigeName)))
											.replace("%nextprestige%", loopNextPrestigeName)
											.replace("%nextprestige_display%", loopNextPrestigeDisplay)
											);
								});
							}
							List<String> continuousCommands = settingEntry.getValue().getCommands();
							if(!isNullOrEmpty(continuousCommands)) {
								plugin.scheduler.runTask(plugin, () -> {
									continuousCommands.forEach(cmd -> {
										plugin.executeCommand(p, plugin.getString(cmd
												.replace("{number}", loopNextPrestigeName)));
									});
								});
							}
						}
					});
				}
			}
			FINAL_PRESTIGE.put(uuid, loopNextPrestigeName);
			getAPI().setPlayerPrestige(uuid, loopNextPrestigeName);
			PRESTIGE_TIMES.put(uuid, PRESTIGE_TIMES.get(uuid) + 1);
			if(!isOnline(p)) return sendStopSignal(name);
			if(plugin.globalStorage.getBooleanData("PrestigeOptions.ResetRank")) {
				if(!isOnline(p)) return sendStopSignal(name);
				RankUpdateEvent e1 = new RankUpdateEvent(p, RankUpdateCause.RANKSET_BYPRESTIGE, plugin.globalStorage.getStringData("defaultrank"));
				plugin.scheduler.runTask(plugin, () -> Bukkit.getPluginManager().callEvent(e1));
				if(e1.isCancelled()) {
					PRXAPI.TASKED_PLAYERS.remove(name);
				} else {
					if(!isOnline(p)) return sendStopSignal(name);
					plugin.playerStorage.setPlayerRank(p, plugin.globalStorage.getStringData("defaultrank"));
				}
				List<String> prestigeCommands = plugin.globalStorage.getStringListData("PrestigeOptions.prestige-cmds");
				if(!isNullOrEmpty(prestigeCommands)) {
					if(!isOnline(p)) return sendStopSignal(name);
					prestigeCommands.forEach(cmd -> {
						if(cmd.startsWith("[rankpermissions]")) {
							getAPI().allRankAddPermissions.forEach(permission -> {
								plugin.perm.delPermissionAsync(p, permission);
							});
						} else if (cmd.startsWith("[prestigepermissions]")) {
							getAPI().allPrestigeAddPermissions.forEach(permission -> {
								plugin.perm.delPermissionAsync(p, permission);
							});
						} else if (cmd.startsWith("[rebirthpermissions]")) {
							getAPI().allRebirthAddPermissions.forEach(permission -> {
								plugin.perm.delPermissionAsync(p, permission);
							});
						} else {
							plugin.executeCommand(p, cmd);
						}
					});
				}
			}
			return true;
		}

	}

	private static class WhenCompleteWorkload implements Workload {

		private Runnable runnable;

		public WhenCompleteWorkload(Runnable runnable) {
			this.runnable = runnable;
		}

		@Override
		public boolean compute() {
			runnable.run();
			return true;
		}

	}

}
