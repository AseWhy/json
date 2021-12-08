package io.github.asewhy.json;

import io.github.asewhy.processors.support.CommonBuilderWriter;
import io.github.asewhy.processors.support.StreamWrapperWriter;
import io.github.asewhy.processors.support.interfaces.iWriter;
import org.jetbrains.annotations.NotNull;

import java.io.OutputStream;
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
    private final iWriter writer;

    private JsonGenerator(@NotNull iWriter writer, @NotNull DateFormat format) {
        this.writer = writer;
        this.inArray = false;
        this.havePrev = false;
        this.currentFormat = format;
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
            this.writer.write("null");
        } else {
            this.writer.write(String.valueOf(write));
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
            this.writer.write("null");
        } else {
            this.writer.write(HOOK).write(safeJson(write)).write(HOOK);
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

        if(value == null) {
            write((String) null);
        } else {
            if(value instanceof String) {
                write((String) value);
            } else if(value instanceof Number) {
                write((Number) value);
            } else if(value instanceof Enum<?>) {
                write((Enum<?>) value);
            } else if(value instanceof Date) {
                write((Date) value);
            } else {
                write(value.toString());
            }
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
            this.writer.write(HOOK).write(safeJson(write)).write(HOOK).write(FIELD_START);
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
        this.writer.write(OBJ_START);

        return this;
    }

    /**
     * Выводит начало объекта [
     *
     * @param name имя поля с объектом
     * @return генератор {@link JsonGenerator}
     */
    public JsonGenerator writeStartObject(String name) {
        writeField(name);
        writeStartObject();

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
        this.writer.write(OBJ_END);

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
        this.writer.write(ARR_START);
        return this;
    }

    /**
     * Выводит начало массива [
     *
     * @param name имя поля с массивом
     * @return генератор {@link JsonGenerator}
     */
    public JsonGenerator writeStartArray(String name) {
        writeField(name);
        writeStartArray();

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
        this.writer.write(ARR_END);
        return this;
    }

    /**
     * Проверяет, имеется ли "сзади" что-либо требующее запятой, если есть выводит запятую
     */
    private void checkPrev() {
        if(this.havePrev) {
            this.writer.write(COMMA);
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

    /**
     * Преобразовывает генератор к строковому значению
     *
     * @return строковое значение, зависит от реализации {@link iWriter}
     */
    @Override
    public String toString() {
        return this.writer.toString();
    }


    //
    // Методы-фабрики
    //

    public static JsonGenerator from(@NotNull StringBuilder builder) {
        return new JsonGenerator(new CommonBuilderWriter(builder), PARSABLE_DATE_FORMAT);
    }

    public static JsonGenerator from(@NotNull OutputStream stream) {
        return new JsonGenerator(new StreamWrapperWriter(stream), PARSABLE_DATE_FORMAT);
    }

    public static JsonGenerator from(@NotNull StringBuilder builder, @NotNull DateFormat format) {
        return new JsonGenerator(new CommonBuilderWriter(builder), format);
    }

    public static JsonGenerator from(@NotNull OutputStream stream, @NotNull DateFormat format) {
        return new JsonGenerator(new StreamWrapperWriter(stream), format);
    }

    public static JsonGenerator from(@NotNull DateFormat format) {
        return new JsonGenerator(new CommonBuilderWriter(new StringBuilder()), format);
    }

    public static JsonGenerator common() {
        return new JsonGenerator(new CommonBuilderWriter(new StringBuilder()), PARSABLE_DATE_FORMAT);
    }
}
