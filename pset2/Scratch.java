public class Scratch {
    public static void main(String[] args) {
        String var1 = "asdf";  
        compare(var1);
        int var2 = 23;
        compare(var2);
        double var3 = 13.4;
        compare(var3);
        char var4 = 'a';
        compare(var4);
        String var5 = "2";
        compare(var5);
    }

    public static void compare(Object var) {
        System.out.print("Type of ");
        System.out.print(var);
        System.out.print(": ");
        System.out.println(var.getClass());

        if (var.getClass() == String.class) {
            System.out.println("String");
        }
        if (var.getClass() == Double.class) {
            System.out.println("Double");
        }

        if (var.getClass() == Integer.class) {
            System.out.println("Integer");
        }

        if (var.getClass() == Character.class) {
            System.out.println("Character");
        }

        if (var.getClass() == Class.class) {
            System.out.println("Class");
        }

        System.out.println();
    }
}