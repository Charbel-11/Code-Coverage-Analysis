import java.util.List;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;


public class InstrumenterClassVisitor extends ClassVisitor implements Opcodes {
	// EECE 334: INSERT CODE HERE
	String className;

	public InstrumenterClassVisitor(final int api, final ClassVisitor classVisitor) {
		super(api, classVisitor);
	}

	public void visit(
			final int version,
			final int access,
			final String name,
			final String signature,
			final String superName,
			final String[] interfaces) {
		if (cv != null) {
			cv.visit(version, access, name, signature, superName, interfaces);
		}

		// EECE 334: INSERT CODE HERE
		className = name;
	}


	public FieldVisitor visitField(
			final int access,
			final String name,
			final String descriptor,
			final String signature,
			final Object value) {
		if (cv != null) {
			return cv.visitField(access, name, descriptor, signature, value);
		}
		return null;
	}

	public MethodVisitor visitMethod(
			final int access,
			final String name,
			final String descriptor,
			final String signature,
			final String[] exceptions) {
		MethodVisitor sinkMethodVisitor = null;
		if (cv != null) {
			sinkMethodVisitor = cv.visitMethod(access, name, descriptor, signature, exceptions);
		}
		
		MyMethodVisitor mv = new MyMethodVisitor(api, sinkMethodVisitor, className, name, descriptor);
		BasicBlockGeneratorMethodVisitor mv2 = new BasicBlockGeneratorMethodVisitor(api, mv);
		return mv2;
	}

	public void visitEnd() {
		if (cv != null) {
			cv.visitEnd();
		}
	}
}



class MyMethodVisitor extends BasicBlockMethodVisitor implements Opcodes{
	// EECE 334: INSERT CODE HERE
	String className;
	String methodName;
	String methodSignature;

	public MyMethodVisitor(final int api, final MethodVisitor methodVisitor, String className, String methodName, String methodSignature) {
		super(api, methodVisitor); // This call will save methodVisitor in the field mv

		// EECE 334: INSERT CODE HERE
		this.className = className;
		this.methodName = methodName;
		this.methodSignature = methodSignature;
	}


	/** Starts the visit of the method's code, if any (i.e. non abstract method). */
	public void visitCode() {
		if (mv != null) {
			mv.visitCode();

			Profiler.methodCount++;
			
			// EECE 334: INSERT CODE HERE
			mv.visitLdcInsn(className);
			mv.visitLdcInsn(methodName);
			mv.visitLdcInsn(methodSignature);
			mv.visitMethodInsn(INVOKESTATIC, "Profiler", "handleMethodEntry", 
					"(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", false);
		}
	}

	public void visitMethodInsn(
			final int opcode,
			final String owner,
			final String name,
			final String descriptor,
			final boolean isInterface) {


		if (mv != null) {
			// EECE 334: INSERT CODE HERE
			 mv.visitLdcInsn(className);
			 mv.visitLdcInsn(methodName);
			 mv.visitLdcInsn(methodSignature);
			 mv.visitLdcInsn(owner);
			 mv.visitLdcInsn(name);
			 mv.visitLdcInsn(descriptor);
			 mv.visitMethodInsn(INVOKESTATIC, "Profiler", "handleMethodInvoke", 
					  "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", false);
		      			
			 mv.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
		}
	}

	public void visitBasicBlock(int count) {
		// EECE 334: INSERT CODE HERE	
		if (mv != null) {
			Profiler.basicBlockCount++;
			
			mv.visitLdcInsn(className);		
			mv.visitLdcInsn(methodName);
			mv.visitLdcInsn(methodSignature);
			mv.visitLdcInsn(Integer.toString(count));
			mv.visitMethodInsn(INVOKESTATIC, "Profiler", "handleBasicBlockEntry", 
					"(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", false);
		}
	}
}