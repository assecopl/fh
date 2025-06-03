package pl.fhframework.compiler.core.generator;

import pl.fhframework.compiler.core.dynamic.DynamicClassArea;
import pl.fhframework.compiler.core.dynamic.DynamicClassFileDescriptor;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.compiler.core.dynamic.IClassInfo;
import pl.fhframework.subsystems.Subsystem;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Interface of addtional type resolver for BindingParser
 */
public interface ITypeProvider {

    /**
     * Returns type supported by this resolver
     * @return type supported by this resolver
     */
    public Type getSupportedType();

    /**
     * Returns methods of this implementation supported type
     * @return methods of this implementation supported type
     */
    public List<MethodDescriptor> getMethods(Type ofType);


    /**
     * Returns methods of this implementation supported type that context has acces to based on permissions
     * @return methods of this implementation supported type
     */
    default List<MethodDescriptor> getMethods(Type ofType, boolean onlyPermitted) {
        return getMethods(ofType);
    }

    /**
     * Returns type literal to be used in generated java class
     * @return type literal to be used in generated java class
     */
    public String toTypeLiteral();

    default Type resolvePartsType(Type ofType, String parts) {
        return getSupportedType();
    }

    default MethodDescriptor getResolvedMethod(Type ofType, String parts) {
        return null;
    }

    default boolean isGroupingElement() {
        return false;
    }

    class ClassInfo implements IClassInfo {
        private boolean isDynamic;

        private DynamicClassName className;

        private String fullClassName;

        public ClassInfo(boolean isDynamic, DynamicClassName className, String fullClassName) {
            this.isDynamic = isDynamic;
            this.className = className;
            this.fullClassName = fullClassName;
        }

        @Override
        public DynamicClassFileDescriptor getXmlFile() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Subsystem getSubsystem() {
            throw new UnsupportedOperationException();
        }

        @Override
        public DynamicClassArea getArea() {
            throw new UnsupportedOperationException();
        }

        @Override
        public DynamicClassName getClassName() {
            return className;
        }

        public String getFullClassName() {
            return fullClassName;
        }

        @Override
        public boolean isDynamic() {
            return isDynamic;
        }
    }
}
