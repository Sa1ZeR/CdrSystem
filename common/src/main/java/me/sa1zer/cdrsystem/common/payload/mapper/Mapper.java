package me.sa1zer.cdrsystem.common.payload.mapper;

/**
 *  can convert pojo to dto
 * @param <F> from object
 * @param <T> to object
 */
public interface Mapper<F, T> {

    T map(F from);
}
