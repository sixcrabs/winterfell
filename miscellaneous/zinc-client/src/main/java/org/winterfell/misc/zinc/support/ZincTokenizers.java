package org.winterfell.misc.zinc.support;

/**
 * <p>
 * zinc 内置支持的分词器
 * </p>
 *
 * @author alex
 * @version v1.0 2022/10/31
 */
public enum ZincTokenizers {

    //
    character,
    char_group,
    ngram,
    edge_ngram,
    exception,
    letter,
    simple,
    lower_case,
    path_hierarchy,
    regexp,
    single,
    keyword,
    // 标准分词器
    standard,
    web,
    whitespace,
    //    Chinese analyzers
    gse_standard,
    gse_search,

}
