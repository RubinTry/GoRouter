package cn.gorouter.gorouter_compiler.utils;


import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import cn.gorouter.gorouter_annotation.TypeKind;

import static cn.gorouter.gorouter_compiler.utils.Consts.BOOLEAN;
import static cn.gorouter.gorouter_compiler.utils.Consts.BYTE;
import static cn.gorouter.gorouter_compiler.utils.Consts.CHAR;
import static cn.gorouter.gorouter_compiler.utils.Consts.DOUBEL;
import static cn.gorouter.gorouter_compiler.utils.Consts.FLOAT;
import static cn.gorouter.gorouter_compiler.utils.Consts.INTEGER;
import static cn.gorouter.gorouter_compiler.utils.Consts.LONG;
import static cn.gorouter.gorouter_compiler.utils.Consts.PARCELABLE;
import static cn.gorouter.gorouter_compiler.utils.Consts.SERIALIZABLE;
import static cn.gorouter.gorouter_compiler.utils.Consts.SHORT;
import static cn.gorouter.gorouter_compiler.utils.Consts.STRING;

public class TypeUtils {
private Types types;
    private TypeMirror parcelableType;
    private TypeMirror serializableType;
    public TypeUtils(javax.lang.model.util.Types types, Elements elements) {
        this.types = types;

        parcelableType = elements.getTypeElement(PARCELABLE).asType();
        serializableType = elements.getTypeElement(SERIALIZABLE).asType();
    }


    /**
     * Diagnostics out the true java type
     *
     * @param element Raw type
     * @return Type class of java
     */
    public int typeExchange(Element element) {
        TypeMirror typeMirror = element.asType();

        // Primitive
        if (typeMirror.getKind().isPrimitive()) {
            return element.asType().getKind().ordinal();
        }

        switch (typeMirror.toString()) {
            case BYTE:
                return TypeKind.BYTE.ordinal();
            case SHORT:
                return TypeKind.SHORT.ordinal();
            case INTEGER:
                return TypeKind.INT.ordinal();
            case LONG:
                return TypeKind.LONG.ordinal();
            case FLOAT:
                return TypeKind.FLOAT.ordinal();
            case DOUBEL:
                return TypeKind.DOUBLE.ordinal();
            case BOOLEAN:
                return TypeKind.BOOLEAN.ordinal();
            case CHAR:
                return TypeKind.CHAR.ordinal();
            case STRING:
                return TypeKind.STRING.ordinal();
            default:
                // Other side, maybe the PARCELABLE or SERIALIZABLE or OBJECT.
                if (types.isSubtype(typeMirror, parcelableType)) {
                    // PARCELABLE
                    return TypeKind.PARCELABLE.ordinal();
                } else if (types.isSubtype(typeMirror, serializableType)) {
                    // SERIALIZABLE
                    return TypeKind.SERIALIZABLE.ordinal();
                } else {
                    return TypeKind.OBJECT.ordinal();
                }
        }
    }
}
