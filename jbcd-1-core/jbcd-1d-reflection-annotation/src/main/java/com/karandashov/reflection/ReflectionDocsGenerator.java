package com.karandashov.reflection;

public class ReflectionDocsGenerator {

    public static void printObjectDescription(Object object) {

        System.out.println("Описание класса:");

        Class<?> clazz = object.getClass();

        System.out.println("- Название: " + clazz.getName());

        describeFields(clazz);

        describeMethods(clazz);
    }

    private static void describeFields(Class<?> clazz) {
        System.out.println("- Поля:");
        var fields = clazz.getDeclaredFields();
        if (fields.length == 0) {
            System.out.println("  (нет)");
            return;
        }
        for (var field : fields) {
            int mods = field.getModifiers();
            String visibility = toVisibility(mods);
            String type = field.getType().getSimpleName();
            System.out.println("  " + visibility + " " + type + " " + field.getName());
        }
    }

    private static void describeMethods(Class<?> clazz) {
        System.out.println("- Методы:");
        var methods = clazz.getDeclaredMethods();
        if (methods.length == 0) {
            System.out.println("  (нет)");
            return;
        }
        for (var method : methods) {
            int mods = method.getModifiers();
            String visibility = toVisibility(mods);
            String returnType = method.getReturnType().getSimpleName();

            Class<?>[] paramTypes = method.getParameterTypes();
            StringBuilder params = new StringBuilder();
            for (int i = 0; i < paramTypes.length; i++) {
                if (i > 0) params.append(", ");
                params.append(paramTypes[i].getSimpleName());
            }

            System.out.println("  " + visibility + " " + returnType + " " + method.getName() + "(" + params + ")");
        }
    }

    private static String toVisibility(int modifiers) {
        if (java.lang.reflect.Modifier.isPublic(modifiers)) return "public";
        if (java.lang.reflect.Modifier.isProtected(modifiers)) return "protected";
        if (java.lang.reflect.Modifier.isPrivate(modifiers)) return "private";
        return "package-private";
    }
}
