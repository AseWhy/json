package io.github.asewhy.json;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public final class JsonGenerator {
    private static final DateFormat PARSABLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

    private static final String HOOK = "\"";
    private static final String COMMA = ",";
    private static final String OBJ_START = "{";
    private static final String OBJ_END = "}";
    private static final String ARR_START = "[";
    private static final String ARR_END = "]";
    private static final String FIELD_START = ":";

    private boolean inArray;
    private boolean havePrev;
    private DateFormat currentFormat;
    private final StringBuilder builder;

    public JsonGenerator(@NotNull StringBuilder builder, @NotNull DateFormat format) {
        this.builder = builder;
        this.inArray = false;
        this.havePrev = false;
        this.currentFormat = format;
    }

    public JsonGenerator(@NotNull DateFormat format) {
        this(new StringBuilder(), format);
    }

    public JsonGenerator() {
        this(new StringBuilder(), PARSABLE_DATE_FORMAT);
    }

    /**
     * Установить текущий формат даты
     *
     * @param currentFormat текущий формат даты
     */
    public void setCurrentFormat(@NotNull final DateFormat currentFormat) {
        this.currentFormat = currentFormat;
    }

    /**
     * Получить текущий формат даты
     *
     * @return текущий формат даты
     */
    public DateFormat getCurrentFormat() {
        return currentFormat;
    }

    /**
     * Выводит энум как значение
     *
     * @return генератор {@link JsonGenerator}
     */
    public JsonGenerator write(Enum<?> write) {
        return this.write(write != null ? write.name() : null);
    }

    /**
     * Выводит дату как значение
     *
     * @return генератор {@link JsonGenerator}
     */
    public JsonGenerator write(Date write) {
        return this.write(write != null ? currentFormat.format(write) : null);
    }

    /**
     * Выводит число как значение
     *
     * @return генератор {@link JsonGenerator}
     */
    public JsonGenerator write(Number write) {
        if(this.inArray) {
            checkPrev();
        }

        if(write == null) {
            this.builder.append("null");
        } else {
            if (write instanceof Double) {
                this.builder.append(String.format("%.2f", write));
            } else if (write instanceof BigDecimal) {
                this.builder.append(String.format("%.2f", write));
            } else {
                this.builder.append(write);
            }
        }

        return this;
    }

    /**
     * Выводит строку как значение
     *
     * @return генератор {@link JsonGenerator}
     */
    public JsonGenerator write(String write) {
        if(this.inArray) {
            checkPrev();
        }

        if(write == null) {
            this.builder.append("null");
        } else {
            this.builder.append(HOOK).append(safeJson(write)).append(HOOK);
        }

        return this;
    }

    /**
     * Выводит тег поля и его значение "название поля": ?"значение"
     *
     * @return генератор {@link JsonGenerator}
     */
    public JsonGenerator writeField(String name, Object value) {
        writeField(name);

        if(value instanceof String) {
            write((String) value);
        } else if(value instanceof Number) {
            write((Number) value);
        } else if(value instanceof Enum<?>) {
            write((Enum<?>) value);
        } else if(value instanceof Date) {
            write((Date) value);
        } else {
            write("");
        }

        return this;
    }

    /**
     * Выводит тег поля "название поля":
     *
     * @return генератор {@link JsonGenerator}
     */
    public JsonGenerator writeField(String write) {
        if(!this.inArray) {
            checkPrev();
            this.builder.append(HOOK).append(safeJson(write)).append(HOOK).append(FIELD_START);
        }

        return this;
    }

    /**
     * Выводит начало объекта [
     *
     * @return генератор {@link JsonGenerator}
     */
    public JsonGenerator writeStartObject() {
        if(this.inArray) {
            checkPrev();
        }

        this.havePrev = false;
        this.inArray = false;
        this.builder.append(OBJ_START);
        return this;
    }

    /**
     * Выводит конец объекта }
     *
     * @return генератор {@link JsonGenerator}
     */
    public JsonGenerator writeEndObject() {
        this.havePrev = true;
        this.inArray = false;
        this.builder.append(OBJ_END);
        return this;
    }

    /**
     * Выводит начало массива [
     *
     * @return генератор {@link JsonGenerator}
     */
    public JsonGenerator writeStartArray() {
        if(this.inArray) {
            checkPrev();
        }

        this.havePrev = false;
        this.inArray = true;
        this.builder.append(ARR_START);
        return this;
    }

    /**
     * Выводит конец массива ]
     *
     * @return генератор {@link JsonGenerator}
     */
    public JsonGenerator writeEndArray() {
        this.havePrev = true;
        this.inArray = false;
        this.builder.append(ARR_END);
        return this;
    }

    /**
     * Проверяет, имеется ли "сзади" что-либо требующее запятой, если есть выводит запятую
     */
    private void checkPrev() {
        if(this.havePrev) {
            this.builder.append(COMMA);
        } else {
            this.havePrev = true;
        }
    }

    /**
     * Обезопасить строку json от кавычек...
     *
     * @param some строка
     * @return безопасная строка
     */
    private static String safeJson(String some) {
        return some.replaceAll("\"", "\\\\\"");
    }

    @Override
    public String toString() {
        return this.builder.toString();
    }
}
