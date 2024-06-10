import java.util.ArrayList;

import jminusminus.CLEmitter;

import static jminusminus.CLConstants.*;

/**
 * This class programmatically generates the class file for the following Java application:
 * 
 * <pre>
 * public class IsPrime {
 *     // Entry point.
 *     public static void main(String[] args) {
 *         int n = Integer.parseInt(args[0]);
 *         boolean result = isPrime(n);
 *         if (result) {
 *             System.out.println(n + " is a prime number");
 *         } else {
 *             System.out.println(n + " is not a prime number");
 *         }
 *     }
 *
 *     // Returns true if n is prime, and false otherwise.
 *     private static boolean isPrime(int n) {
 *         if (n < 2) {
 *             return false;
 *         }
 *         for (int i = 2; i <= n / i; i++) {
 *             if (n % i == 0) {
 *                 return false;
 *             }
 *         }
 *         return true;
 *     }
 * }
 * </pre>
 */
public class GenIsPrime {
    public static void main(String[] args) {
        CLEmitter e = new CLEmitter(true);
        
        // ArrayList instance to store modifiers
        ArrayList<String> modifiers = new ArrayList<String>();

        // public class isPrime {
        modifiers.add("public");
        e.addClass(modifiers, "IsPrime", "java/lang/Object", null, true);

        // public static void main(String[] args) {
        modifiers.clear();
        modifiers.add("public");
        modifiers.add("static");
        e.addMethod(modifiers, "main", "([Ljava/lang/String;)V", null, true);

        // int n = Integer.parseInt(args[0]);
        e.addNoArgInstruction(ALOAD_0);
        e.addNoArgInstruction(ICONST_0);
        e.addNoArgInstruction(AALOAD);
        e.addMemberAccessInstruction(INVOKESTATIC, "java/lang/Integer", "parseInt",
                "(Ljava/lang/String;)I");
        e.addNoArgInstruction(ISTORE_1); // n

        // boolean result = isPrime(n);
        e.addNoArgInstruction(ILOAD_1); // n
        e.addMemberAccessInstruction(INVOKESTATIC, "IsPrime", "isPrime", "(I)Z");
        e.addNoArgInstruction(ISTORE_2); // result


        // Get System.out on stack
        e.addMemberAccessInstruction(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");

        // Create an intance (say sb) of StringBuffer on stack for string concatenations
        //    sb = new StringBuffer();
        e.addReferenceInstruction(NEW, "java/lang/StringBuffer");
        e.addNoArgInstruction(DUP);
        e.addMemberAccessInstruction(INVOKESPECIAL, "java/lang/StringBuffer", "<init>", "()V");

        // sb.append(n);
        e.addNoArgInstruction(ILOAD_1); // n
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/lang/StringBuffer", "append",
                "(I)Ljava/lang/StringBuffer;");

        e.addNoArgInstruction(ILOAD_2); // result
        e.addBranchInstruction(IFNE, "prime");

        e.addLDCInstruction(" is not a prime number");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/lang/StringBuffer", "append",
                "(Ljava/lang/String;)Ljava/lang/StringBuffer;");
        e.addBranchInstruction(GOTO, "print");

        // sb.append(" is a prime number");
        e.addLabel("prime");
        e.addLDCInstruction(" is a prime number");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/lang/StringBuffer", "append",
                "(Ljava/lang/String;)Ljava/lang/StringBuffer;");


        // System.out.println(sb.toString());
        e.addLabel("print");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/lang/StringBuffer",
                "toString", "()Ljava/lang/String;");
        e.addMemberAccessInstruction(INVOKEVIRTUAL, "java/io/PrintStream", "println",
                "(Ljava/lang/String;)V");

        // return;
        e.addNoArgInstruction(RETURN);

        // private static boolean isPrime(int n) {
        modifiers.clear();
        modifiers.add("private");
        modifiers.add("static");
        e.addMethod(modifiers, "isPrime", "(I)Z", null, true);


        // if n >= 2, branch to InitializeLoop
        e.addNoArgInstruction(ILOAD_0); // n
        e.addNoArgInstruction(ICONST_2);
        e.addBranchInstruction(IF_ICMPGE, "InitializeLoop");

        // Base case: return false;
        e.addNoArgInstruction(ICONST_0);
        e.addNoArgInstruction(IRETURN);

        // i = 2
        e.addLabel("InitializeLoop");
        e.addNoArgInstruction(ICONST_2);
        e.addNoArgInstruction(ISTORE_1); // Store in offset 1

        // if i > n/i, branch to ReturnTrue
        e.addLabel("CheckLoop");
        e.addNoArgInstruction(ILOAD_0); // n
        e.addNoArgInstruction(ILOAD_1); // i
        e.addNoArgInstruction(IDIV); // n/i
        e.addNoArgInstruction(ILOAD_1); // i
        e.addBranchInstruction(IF_ICMPLT, "ReturnTrue"); // n/i < i

        //if n % i != 0, goto IncrementLoop
        e.addNoArgInstruction(ILOAD_0); // n
        e.addNoArgInstruction(ILOAD_1); // i
        e.addNoArgInstruction(IREM); // n % i
        e.addBranchInstruction(IFNE, "IncrementLoop"); // n % i != 0

        // return false;
        e.addNoArgInstruction(ICONST_0);
        e.addNoArgInstruction(IRETURN);

        // Increment i by 1
        e.addLabel("IncrementLoop");
        e.addNoArgInstruction(ILOAD_1); // i
        e.addNoArgInstruction(ICONST_1); // 1
        e.addNoArgInstruction(IADD); // i + 1
        e.addNoArgInstruction(ISTORE_1); // store i+1
        e.addBranchInstruction(GOTO, "CheckLoop");

        // Return true
        e.addLabel("ReturnTrue");
        e.addNoArgInstruction(ICONST_1);
        e.addNoArgInstruction(IRETURN);

        e.write();
    }
}
