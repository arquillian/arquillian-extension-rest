package org.jboss.arquillian.extension.rest.client;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.AnnotationMemberValue;
import javassist.bytecode.annotation.ArrayMemberValue;
import javassist.bytecode.annotation.BooleanMemberValue;
import javassist.bytecode.annotation.ByteMemberValue;
import javassist.bytecode.annotation.CharMemberValue;
import javassist.bytecode.annotation.ClassMemberValue;
import javassist.bytecode.annotation.DoubleMemberValue;
import javassist.bytecode.annotation.EnumMemberValue;
import javassist.bytecode.annotation.FloatMemberValue;
import javassist.bytecode.annotation.IntegerMemberValue;
import javassist.bytecode.annotation.LongMemberValue;
import javassist.bytecode.annotation.MemberValue;
import javassist.bytecode.annotation.ShortMemberValue;
import javassist.bytecode.annotation.StringMemberValue;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public final class ClassModifier {

    private static final LongHolder COUNTER = new LongHolder();

    private ClassModifier()
    {
    }

    private static void addNewAnnotations(Annotation[] add, CtMethod method, ConstPool constpool, ClassPool classPool, AnnotationsAttribute attr)
        throws IllegalAccessException, NotFoundException, InvocationTargetException
    {
        if (null == attr) {
            attr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);
            method.getMethodInfo().addAttribute(attr);
        }
        for (Annotation toAdd : add) {
            attr.addAnnotation(toJavassist(toAdd, constpool, classPool));
        }
    }

    private static AnnotationsAttribute filterExistingAnnotations(Annotation[] remove, CtMethod method)
    {
        AnnotationsAttribute attr = null;
        final List attributes = method.getMethodInfo().getAttributes();
        for (Object attribute : attributes) {
            if (attribute instanceof AnnotationsAttribute) {
                attr = (AnnotationsAttribute) attribute;
                final List<javassist.bytecode.annotation.Annotation> annotations = new ArrayList<javassist.bytecode.annotation.Annotation>();
                for (javassist.bytecode.annotation.Annotation annotation : attr.getAnnotations()) {
                    boolean shouldRemove = false;
                    for (Annotation annotationToRemove : remove) {
                        if (annotationToRemove.annotationType().getCanonicalName().equals(annotation.getTypeName())) {
                            shouldRemove = true;
                        }
                    }
                    if (!shouldRemove) {
                        annotations.add(annotation);
                    }
                }
                attr.setAnnotations(annotations.toArray(new javassist.bytecode.annotation.Annotation[annotations.size()]));
            }
        }
        return attr;
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<T> getModifiedClass(Class<T> clazz, Annotation[] add)
        throws javassist.NotFoundException, CannotCompileException, InvocationTargetException, IllegalAccessException
    {
        ClassPool pool = ClassPool.getDefault();
        CtClass cc = pool.get(clazz.getCanonicalName());
        for (CtMethod method : cc.getDeclaredMethods()) {
            ClassFile ccFile = cc.getClassFile();
            ConstPool constpool = ccFile.getConstPool();
            AnnotationsAttribute attr;
            attr = filterExistingAnnotations(add, method);
            addNewAnnotations(add, method, constpool, pool, attr);
        }
        cc.setName(cc.getName() + "$ClassModifier$" + COUNTER.increment());
        cc.setSuperclass(pool.get(clazz.getCanonicalName()));
        return cc.toClass();
    }

    private static CtClass getType(Object value, ClassPool classPool) throws NotFoundException
    {
        if (value instanceof Boolean) {
            return CtClass.booleanType;
        } else if (value instanceof Byte) {
            return CtClass.byteType;
        } else if (value instanceof Character) {
            return CtClass.charType;
        } else if (value instanceof Short) {
            return CtClass.shortType;
        } else if (value instanceof Integer) {
            return CtClass.intType;
        } else if (value instanceof Long) {
            return CtClass.longType;
        } else if (value instanceof Float) {
            return CtClass.floatType;
        } else if (value instanceof Double) {
            return CtClass.doubleType;
        } else {
            return classPool.get(value.getClass().getCanonicalName());
        }
    }

    private static javassist.bytecode.annotation.Annotation toJavassist(Annotation annotation, ConstPool constpool, ClassPool classPool)
        throws NotFoundException, InvocationTargetException, IllegalAccessException
    {
        final javassist.bytecode.annotation.Annotation newAnnotation = new javassist.bytecode.annotation.Annotation(
            annotation.annotationType().getCanonicalName(), constpool);
        for (Method method : annotation.annotationType().getDeclaredMethods()) {
            final Object value = method.invoke(annotation);
            newAnnotation.addMemberValue(method.getName(), toMemberValue(value, constpool, classPool));
        }
        return newAnnotation;
    }

    private static MemberValue toMemberValue(Object value, ConstPool constpool, ClassPool classPool) throws NotFoundException
    {
        final CtClass type = getType(value, classPool);
        final MemberValue memberValue = javassist.bytecode.annotation.Annotation.createMemberValue(constpool, type);
        if (memberValue instanceof BooleanMemberValue) {
            ((BooleanMemberValue) memberValue).setValue((Boolean) value);
        } else if (memberValue instanceof ByteMemberValue) {
            ((ByteMemberValue) memberValue).setValue((Byte) value);
        } else if (memberValue instanceof CharMemberValue) {
            ((CharMemberValue) memberValue).setValue((Character) value);
        } else if (memberValue instanceof ShortMemberValue) {
            ((ShortMemberValue) memberValue).setValue((Short) value);
        } else if (memberValue instanceof IntegerMemberValue) {
            ((IntegerMemberValue) memberValue).setValue((Integer) value);
        } else if (memberValue instanceof LongMemberValue) {
            ((LongMemberValue) memberValue).setValue((Long) value);
        } else if (memberValue instanceof FloatMemberValue) {
            ((FloatMemberValue) memberValue).setValue((Float) value);
        } else if (memberValue instanceof DoubleMemberValue) {
            ((DoubleMemberValue) memberValue).setValue((Double) value);
        } else if (memberValue instanceof ClassMemberValue) {
            ((ClassMemberValue) memberValue).setValue(((Class) value).getCanonicalName());
        } else if (memberValue instanceof StringMemberValue) {
            ((StringMemberValue) memberValue).setValue((String) value);
        } else if (type.isArray()) {
            ((ArrayMemberValue) memberValue).setValue(toMemberValue((Object[]) value, constpool, classPool));
        } else if (type.isInterface()) {
            ((AnnotationMemberValue) memberValue).setValue((javassist.bytecode.annotation.Annotation) value);
        } else {
            ((EnumMemberValue) memberValue).setValue((String) value);
        }
        return memberValue;
    }

    private static MemberValue[] toMemberValue(Object[] value, ConstPool constpool, ClassPool classPool) throws NotFoundException
    {
        final MemberValue[] memberValues = new MemberValue[value.length];
        for (int i = 0; i < value.length; i++) {
            memberValues[i] = toMemberValue(value[i], constpool, classPool);
        }
        return memberValues;
    }

    private static class LongHolder {

        private Long value = 1L;

        private synchronized long increment()
        {
            return value++;
        }
    }
}
