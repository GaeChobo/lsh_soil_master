//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package kr.movements.smv2.util;

import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.AbstractMap;
import kr.movements.smv2.common.exception.BootException;
import org.springframework.stereotype.Service;

@Service("convertUtilService")
public class ConvertUtil {
    protected static int nullNumber = Integer.MIN_VALUE;
    protected static ConvertUtil instance = new ConvertUtil();

    protected ConvertUtil() {
    }

    public static ConvertUtil getInstance() {
        return instance;
    }

    public static ConvertUtil getInstance(int nullNumber) {
        ConvertUtil.nullNumber = nullNumber;
        return instance;
    }

    public boolean isNOE(Object... value) {
        try {
            boolean result = true;

            for(int i = 0; i < value.length; ++i) {
                if (this.isNNNNE(value[i])) {
                    result = false;
                    break;
                }
            }

            return result;
        } catch (Exception var4) {
            throw new BootException(var4);
        }
    }

    public boolean isNOE(Object value) {
        if (value == null) {
            return true;
        } else {
            if (value instanceof AbstractCollection) {
                if (value instanceof AbstractList) {
                    if (((AbstractCollection)value).isEmpty()) {
                        return true;
                    }
                } else if (value instanceof AbstractMap && ((AbstractMap)value).isEmpty()) {
                    return true;
                }
            } else {
                if (value instanceof String && (value.equals("") || ((String)value).isEmpty())) {
                    return true;
                }

                if (value instanceof Double && (Double)value == (double)nullNumber || value instanceof Float && (Float)value == (float)nullNumber || value instanceof Long && (Long)value == (long)nullNumber || value instanceof Integer && (Integer)value == nullNumber) {
                    return true;
                }

                if (value.toString().equals("") || value.toString().isEmpty()) {
                    return true;
                }
            }

            return false;
        }
    }

    public boolean isNNNNE(Object... value) {
        try {
            boolean result = true;

            for(int i = 0; i < value.length; ++i) {
                if (this.isNOE(value[i])) {
                    result = false;
                    break;
                }
            }

            return result;
        } catch (Exception var4) {
            throw new BootException(var4);
        }
    }

    public boolean isNNNNE(Object value) {
        if (value == null) {
            return false;
        } else {
            if (value instanceof AbstractCollection) {
                if (value instanceof AbstractList) {
                    if (((AbstractCollection)value).isEmpty()) {
                        return false;
                    }
                } else if (value instanceof AbstractMap && ((AbstractMap)value).isEmpty()) {
                    return false;
                }
            } else {
                if (value instanceof String && (value.equals("") || ((String)value).isEmpty())) {
                    return false;
                }

                if (value instanceof Double && (Double)value == (double)nullNumber || value instanceof Float && (Float)value == (float)nullNumber || value instanceof Long && (Long)value == (long)nullNumber || value instanceof Integer && (Integer)value == nullNumber) {
                    return false;
                }

                if (value.toString().equals("") || value.toString().isEmpty()) {
                    return false;
                }
            }

            return true;
        }
    }

    public double num(Object value) {
        try {
            if (this.isNNNNE(value)) {
                if (value instanceof String) {
                    return Double.parseDouble((String)value);
                }

                if (value instanceof Double) {
                    return (Double)value;
                }

                if (value instanceof Float) {
                    return (double)(Float)value;
                }

                if (value instanceof Long) {
                    return (double)(Long)value;
                }

                if (value instanceof Integer) {
                    return (double)(Integer)value;
                }
            }

            return (double)nullNumber;
        } catch (Exception var3) {
            throw new BootException(var3);
        }
    }

    public boolean bool(Object value) {
        try {
            return this.isNNNNE(value) && (value instanceof String && ("true".equals(String.valueOf(value).toLowerCase()) || "y".equals(String.valueOf(value).toLowerCase())) || value instanceof Double && ((Double)value).intValue() == 1 || value instanceof Float && ((Float)value).intValue() == 1 || value instanceof Long && ((Long)value).intValue() == 1 || value instanceof Integer && (Integer)value == 1 || value instanceof Boolean && (Boolean)value);
        } catch (Exception var3) {
            throw new BootException(var3);
        }
    }

    public int bToi(Object value) {
        try {
            if (this.isNNNNE(value) && (value instanceof String && ("true".equals(String.valueOf(value).toLowerCase()) || "y".equals(String.valueOf(value).toLowerCase())) || value instanceof Double && ((Double)value).intValue() == 1 || value instanceof Float && ((Float)value).intValue() == 1 || value instanceof Long && ((Long)value).intValue() == 1 || value instanceof Integer && (Integer)value == 1 || value instanceof Boolean && (Boolean)value)) {
                return 1;
            } else {
                return !this.isNNNNE(value) || (!(value instanceof String) || !"false".equals(String.valueOf(value).toLowerCase()) && !"n".equals(String.valueOf(value).toLowerCase())) && (!(value instanceof Double) || ((Double)value).intValue() != 0) && (!(value instanceof Float) || ((Float)value).intValue() != 0) && (!(value instanceof Long) || ((Long)value).intValue() != 0) && (!(value instanceof Integer) || (Integer)value != 0) && (!(value instanceof Boolean) || (Boolean)value) ? nullNumber : 0;
            }
        } catch (Exception var3) {
            throw new BootException(var3);
        }
    }

    public static int getNullNumber() {
        return nullNumber;
    }
}
