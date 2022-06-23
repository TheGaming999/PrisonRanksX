package me.prisonranksx.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import sun.misc.Unsafe;

/*
 * AsyncWorldEdit a performance improvement plugin for Minecraft WorldEdit plugin.
 * Copyright (c) 2014, SBPrime <https://github.com/SBPrime/>
 * Copyright (c) AsyncWorldEdit contributors
 *
 * All rights reserved.
 *
 * Redistribution in source, use in source and binary forms, with or without
 * modification, are permitted free of charge provided that the following 
 * conditions are met:
 *
 * 1.  Redistributions of source code must retain the above copyright notice, this
 *     list of conditions and the following disclaimer.
 * 2.  Redistributions of source code, with or without modification, in any form
 *     other then free of charge is not allowed,
 * 3.  Redistributions of source code, with tools and/or scripts used to build the 
 *     software is not allowed,
 * 4.  Redistributions of source code, with information on how to compile the software
 *     is not allowed,
 * 5.  Providing information of any sort (excluding information from the software page)
 *     on how to compile the software is not allowed,
 * 6.  You are allowed to build the software for your personal use,
 * 7.  You are allowed to build the software using a non public build server,
 * 8.  Redistributions in binary form in not allowed.
 * 9.  The original author is allowed to redistrubute the software in bnary form.
 * 10. Any derived work based on or containing parts of this software must reproduce
 *     the above copyright notice, this list of conditions and the following
 *     disclaimer in the documentation and/or other materials provided with the
 *     derived work.
 * 11. The original author of the software is allowed to change the license
 *     terms or the entire license of the software as he sees fit.
 * 12. The original author of the software is allowed to sublicense the software
 *     or its parts using any license terms he sees fit.
 * 13. By contributing to this project you agree that your contribution falls under this
 *     license.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
/**
 * 
 * Simple utility to alter bukkit events <b>EventPriority</b> at runtime using Reflection.
 * <p><i>Note:</i> LOWEST is run first, MONITOR is run LAST.</p>
 * <p><b>EventPriority</b> is set to NORMAL by default if it's not specified beside @EventHandler annotation.</p>
 * 
 */
public class EventPriorityManager {

	static {
		if (isJava8Plus()) {
			try {
				openModuleAccess();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private static Unsafe unsafe() {
		return EventPriorityManager.get(Unsafe.class, Unsafe.class, "theUnsafe");
	}

	private static <T> T get(Class<?> sourceClass, Class<T> fieldClass,
			String fieldName) {
		return get(sourceClass, fieldClass, null, fieldName);
	}

	private static <T> T get(Class<?> sourceClass, Class<T> fieldClass, Object instance, String fieldName) {
		try {
			Field field = sourceClass.getDeclaredField(fieldName);
			boolean accessible = field.isAccessible();

			if (!accessible) {
				field.setAccessible(true);
			}

			try {
				return fieldClass.cast(field.get(instance));
			} finally {
				if (!accessible) {
					field.setAccessible(false);
				}
			}
		} catch (IllegalArgumentException ex) {
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		} catch (NoSuchFieldException ex) {
			ex.printStackTrace();
		} catch (SecurityException ex) {
			ex.printStackTrace();
		} catch (ClassCastException ex) {
			ex.printStackTrace();
		}

		return null;
	}

	/**
	 * 
	 * @param listenerClass class that implements listener. Example: <i>playerJoinListener.getClass()</i>
	 * @param eventMethodName the method that has the event. Example: <i>"onPlayerJoin"</i>
	 * @param eventClass the event class that the method uses. Example: <i>PlayerJoinEvent.class</i>
	 * @param newPriority the desired new priority. Example: <i>EventPriority.HIGHEST</i>
	 * @return previous priority.
	 */
	@SuppressWarnings("unchecked")
	public static EventPriority setPriority(Class<?> listenerClass, String eventMethodName, Class<? extends Event> eventClass, EventPriority newPriority) {
		Annotation annotation = null;
		try {
			annotation = listenerClass.getDeclaredMethod(eventMethodName, eventClass).getAnnotation(EventHandler.class);
		} catch (NoSuchMethodException | SecurityException e1) {
			e1.printStackTrace();
		}
		InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation);
		Field memberValuesField;
		try {
			memberValuesField = invocationHandler.getClass().getDeclaredField("memberValues");
		} catch (NoSuchFieldException | SecurityException e) {
			throw new IllegalStateException(e);
		}
		memberValuesField.setAccessible(true);
		Map<String, Object> memberValues = null;
		try {
			memberValues = (Map<String, Object>) memberValuesField.get(invocationHandler);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
		EventPriority oldPriority = (EventPriority)memberValues.get("priority");
		if (oldPriority == null || oldPriority.getClass() != newPriority.getClass()) {
			throw new IllegalArgumentException("Old EventPriority is missing or invalid.");
		}
		memberValues.put("priority", newPriority);
		return (EventPriority)oldPriority;
	}

	/**
	 * Changes the EventPriority of the first found event method that has an EventHandler annotation.
	 * @param listenerClass class that implements listener. Example: <i>playerJoinListener.getClass()</i>
	 * @param newPriority the desired new priority. Example: <i>EventPriority.MONITOR</i>
	 * @return old priority.
	 */
	@SuppressWarnings("unchecked")
	public static EventPriority setPriority(Class<?> listenerClass, EventPriority newPriority) {
		Annotation annotation = null;
		for (Method method : listenerClass.getDeclaredMethods()) {
			if(method.getParameterCount() > 0) {
				Class<?> param = method.getParameterTypes()[0];
				Class<?> superClass = param.getSuperclass();
				if(superClass == null) break;
				Class<?> superDuperClass = superClass.getSuperclass();
				// some events have a super duper class, some don't.
				if((superClass == Event.class || superDuperClass == Event.class) && method.isAnnotationPresent(EventHandler.class)) {
					annotation = method.getAnnotation(EventHandler.class);
					break;
				}
			}
		}
		if(annotation == null) {
			throw new NullPointerException("Unable to find an event method with an EventHandler.");
		}
		InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation);
		Field memberValuesField;
		try {
			memberValuesField = invocationHandler.getClass().getDeclaredField("memberValues");
		} catch (NoSuchFieldException | SecurityException e) {
			throw new IllegalStateException(e);
		}
		memberValuesField.setAccessible(true);
		Map<String, Object> memberValues = null;
		try {
			memberValues = (Map<String, Object>) memberValuesField.get(invocationHandler);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
		EventPriority oldPriority = (EventPriority)memberValues.get("priority");
		if (oldPriority == null || oldPriority.getClass() != newPriority.getClass()) {
			throw new IllegalArgumentException("Old EventPriority is missing or invalid.");
		}
		memberValues.put("priority", newPriority);
		return (EventPriority)oldPriority;
	}

	/**
	 * Changes the EventPriorities of all the event methods that has an EventHandler annotation inside the {@code listenerClass}.
	 * @param listenerClass class that implements listener. Example: <i>playerJoinListener.getClass()</i>
	 * @param newPriority the desired new priority. Example: <i>EventPriority.NORMAL</i>
	 * @return Event method names and their old event priorites.
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, EventPriority> setPriorities(Class<?> listenerClass, EventPriority newPriority) {
		List<Annotation> annotations = new ArrayList<>();
		Map<String, EventPriority> oldPriorities = new LinkedHashMap<>();
		for (Method method : listenerClass.getDeclaredMethods()) {
			if(method.getParameterCount() > 0) {
				Class<?> param = method.getParameterTypes()[0];
				Class<?> superClass = param.getSuperclass();
				if(superClass != null) {
					// some events have a super duper class, some don't.
					Class<?> superDuperClass = superClass.getSuperclass();
					if((superClass == Event.class || superDuperClass == Event.class) && method.isAnnotationPresent(EventHandler.class)) {
						EventHandler annotation = method.getAnnotation(EventHandler.class);
						annotations.add(annotation);
						oldPriorities.put(method.getName(), annotation.priority());
					}
				}
			}
		}
		oldPriorities = Collections.unmodifiableMap(oldPriorities);
		if(annotations.isEmpty()) {
			throw new NullPointerException("Unable to find an event method with an EventHandler.");
		}
		annotations.forEach(annotation -> {
			InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation);
			Field memberValuesField;
			try {
				memberValuesField = invocationHandler.getClass().getDeclaredField("memberValues");
			} catch (NoSuchFieldException | SecurityException e) {
				throw new IllegalStateException(e);
			}
			memberValuesField.setAccessible(true);
			Map<String, Object> memberValues = null;
			try {
				memberValues = (Map<String, Object>) memberValuesField.get(invocationHandler);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new IllegalStateException(e);
			}
			EventPriority oldPriority = (EventPriority)memberValues.get("priority");
			if (oldPriority == null || oldPriority.getClass() != newPriority.getClass()) {
				throw new IllegalArgumentException("Old EventPriority is missing or invalid.");
			}
			memberValues.put("priority", newPriority);
		});
		return oldPriorities;
	}

	/**
	 * Changes the EventPriorities of all the event methods that has an EventHandler annotation inside the <b>listenerClass</b>.
	 * @param listenerClass object that implements listener. Example: <i>playerJoinListener</i> from <p><i>PlayerJoinListener playerJoinListener = new PlayerJoinListener(MainClass);</i></p>
	 * @param priority the desired new priority. Example: <i>EventPriority.NORMAL</i> or as string: <i>"NORMAL"</i>
	 * @return Event method names and their old event priorites.
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, EventPriority> setPriorities(Object listenerClass, Object priority) {
		List<Annotation> annotations = new ArrayList<>();
		Map<String, EventPriority> oldPriorities = new LinkedHashMap<>();
		EventPriority newPriority = matchPriority(priority);
		Class<?> listenerClazz = listenerClass.getClass();
		for (Method method : listenerClazz.getDeclaredMethods()) {
			if(method.getParameterCount() > 0) {
				Class<?> param = method.getParameterTypes()[0];
				Class<?> superClass = param.getSuperclass();
				if(superClass != null) {
					// some events have a super duper class, some don't.
					Class<?> superDuperClass = superClass.getSuperclass();
					if((superClass == Event.class || superDuperClass == Event.class) && method.isAnnotationPresent(EventHandler.class)) {
						EventHandler annotation = method.getAnnotation(EventHandler.class);
						annotations.add(annotation);
						oldPriorities.put(method.getName(), annotation.priority());
					}
				}
			}
		}
		oldPriorities = Collections.unmodifiableMap(oldPriorities);
		if(annotations.isEmpty()) {
			throw new NullPointerException("Unable to find an event method with an EventHandler.");
		}
		annotations.forEach(annotation -> {
			InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation);
			Field memberValuesField;
			try {
				memberValuesField = invocationHandler.getClass().getDeclaredField("memberValues");
			} catch (NoSuchFieldException | SecurityException e) {
				throw new IllegalStateException(e);
			}
			memberValuesField.setAccessible(true);
			Map<String, Object> memberValues = null;
			try {
				memberValues = (Map<String, Object>) memberValuesField.get(invocationHandler);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				throw new IllegalStateException(e);
			}
			EventPriority oldPriority = (EventPriority)memberValues.get("priority");
			if (oldPriority == null || oldPriority.getClass() != newPriority.getClass()) {
				throw new IllegalArgumentException("Old EventPriority is missing or invalid.");
			}
			memberValues.put("priority", newPriority);
		});
		return oldPriorities;
	}

	/**
	 * @param listenerClass class that implements listener. Example: <i>playerJoinListener.getClass()</i>
	 * @return EventPriority of the first found event method with an event handler.
	 */
	public static EventPriority getPriority(Class<?> listenerClass) {
		EventHandler annotation = null;
		for (Method method : listenerClass.getDeclaredMethods()) {
			if(method.getParameterCount() > 0) {
				Class<?> param = method.getParameterTypes()[0];
				Class<?> superClass = param.getSuperclass();
				if(superClass == null) break;
				// some events have a super duper class, some don't.
				Class<?> superDuperClass = superClass.getSuperclass();
				if((superClass == Event.class || superDuperClass == Event.class) && method.isAnnotationPresent(EventHandler.class)) {
					annotation = method.getAnnotation(EventHandler.class);
					break;
				}
			}
		}
		if(annotation == null) {
			throw new NullPointerException("Unable to find an event method with an EventHandler.");
		}
		return annotation.priority();
	}

	/**
	 * @param listenerClass object that implements listener. Example: <i>playerJoinListener</i> from <p><i>PlayerJoinListener <b>playerJoinListener</b> = new PlayerJoinListener(MainClass);</i></p>
	 * @return Event method names and their current event priorites.
	 */
	public static Map<String, EventPriority> getPriorities(Object listenerClass) {
		Map<String, EventPriority> oldPriorities = new LinkedHashMap<>();
		Class<?> listenerClazz = listenerClass.getClass();
		for (Method method : listenerClazz.getDeclaredMethods()) {
			if(method.getParameterCount() > 0) {
				Class<?> param = method.getParameterTypes()[0];
				Class<?> superClass = param.getSuperclass();
				if(superClass != null) {
					// some events have a super duper class, some don't. AsyncPlayerPreLoginEvent for example, doesn't have a super duper class.
					Class<?> superDuperClass = param.getSuperclass().getSuperclass();
					if((superClass == Event.class || superDuperClass == Event.class) && method.isAnnotationPresent(EventHandler.class)) {
						EventHandler annotation = method.getAnnotation(EventHandler.class);
						oldPriorities.put(method.getName(), annotation.priority());
					}
				}
			}
		}
		oldPriorities = Collections.unmodifiableMap(oldPriorities);
		if(oldPriorities.isEmpty()) {
			throw new NullPointerException("Unable to find an event method with an EventHandler.");
		}
		return oldPriorities;
	}

	/**
	 * @param listenerClass object that implements listener. Example: <i>playerJoinListener.getClass()</i>
	 * @return Event method names and their current event priorites.
	 */
	public static Map<String, EventPriority> getPriorities(Class<?> listenerClass) {
		Map<String, EventPriority> oldPriorities = new LinkedHashMap<>();
		for (Method method : listenerClass.getDeclaredMethods()) {
			if(method.getParameterCount() > 0) {
				Class<?> param = method.getParameterTypes()[0];
				Class<?> superClass = param.getSuperclass();
				if(superClass != null) {
					// some events have a super duper class, some don't.
					Class<?> superDuperClass = param.getSuperclass().getSuperclass();
					if((superClass == Event.class || superDuperClass == Event.class) && superDuperClass == Event.class && method.isAnnotationPresent(EventHandler.class)) {
						EventHandler annotation = method.getAnnotation(EventHandler.class);
						oldPriorities.put(method.getName(), annotation.priority());
					}
				}
			}
		}
		oldPriorities = Collections.unmodifiableMap(oldPriorities);
		if(oldPriorities.isEmpty()) {
			throw new NullPointerException("Unable to find an event method with an EventHandler.");
		}
		return oldPriorities;
	}

	/**
	 * 
	 * @param listenerClass class that implements listener. Example: <i>playerChatListener.getClass()</i>
	 * @param eventMethodName the method that has the event. Example: <i>"onPlayerChat"</i>
	 * @param eventClass the event class that the method uses. Example: <i>AsyncPlayerChatEvent.class</i>
	 * @return current priority.
	 */
	public static EventPriority getPriority(Class<?> listenerClass, String eventMethodName, Class<? extends Event> eventClass) {
		EventHandler annotation = null;
		try {
			annotation = listenerClass.getDeclaredMethod(eventMethodName, eventClass).getAnnotation(EventHandler.class);
		} catch (NoSuchMethodException | SecurityException e1) {
			e1.printStackTrace();
		}
		return annotation.priority();
	}
	
	/**
	 * @author SBPrime
	 * @throws NoSuchMethodException
	 * @throws NoSuchFieldException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void openModuleAccess()
			throws NoSuchMethodException, NoSuchFieldException,
			InvocationTargetException, IllegalAccessException,
			ClassNotFoundException {

		final Map.Entry<String, String[]>[] modulesToOpen = new Map.Entry[]{
				new AbstractMap.SimpleEntry("java.base", new String[]{"java.lang", "java.security", "java.util", "sun.reflect.annotation"})
		};
		
		ClassLoader cl = EventPriorityManager.class.getClassLoader();
		Class<?> cModule = cl.loadClass("java.lang.Module");
		Class<?> cModuleLayer = cl.loadClass("java.lang.ModuleLayer");

		Method mModuleImplAddOpens = cModule.getDeclaredMethod("implAddOpens", String.class, cModule);
		Method mModuleLayerFindModule = cModuleLayer.getDeclaredMethod("findModule", String.class);

		Object moduleLayer = cModuleLayer.cast(cModuleLayer.getMethod("boot").invoke(null));
		Object moduleAwe = cModule.cast(Class.class.getDeclaredMethod("getModule").invoke(EventPriorityManager.class));

		Unsafe u = unsafe();
		u.putBooleanVolatile(mModuleImplAddOpens,
				u.objectFieldOffset(HackyClass.class.getDeclaredField("field1")),
				true);

		for (Map.Entry<String, String[]> e : modulesToOpen) {
			Object module = cModule.cast(((Optional<?>)mModuleLayerFindModule.invoke(moduleLayer, e.getKey()))
					.orElseThrow(() -> new RuntimeException("Unable to get '" + e.getKey() + "'")));

			for (String pn : e.getValue()) {
				mModuleImplAddOpens.invoke(module, pn, moduleAwe);
			}
		}
	}

	private static boolean isJava8Plus() {
		try {
			return EventPriorityManager.class.getClassLoader().loadClass("java.lang.Module") != null;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}

	@Nonnull
	private static EventPriority matchPriority(Object priority) {
		if(priority == null) return EventPriority.NORMAL;
		if(priority instanceof EventPriority) {
			return (EventPriority)priority;
		} else if (priority instanceof String) {
			String stringPriority = ((String)priority).toUpperCase();
			switch (stringPriority) {
			case "LOWEST": 
				return EventPriority.LOWEST;
			case "LOW": 
				return EventPriority.LOW;
			case "HIGH": 
				return EventPriority.HIGH;
			case "HIGHEST": 
				return EventPriority.HIGHEST;
			case "MONITOR": 
				return EventPriority.MONITOR;
			default: 
				return EventPriority.NORMAL;
			}
		} else {
			return EventPriority.NORMAL;
		}
	}

	private static class HackyClass {
		@SuppressWarnings("unused")
		boolean field1;
	}

}