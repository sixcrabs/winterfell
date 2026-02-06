package org.winterfell.misc.zinc.action.es.search.query.core;

/**
 * <p>
 * 查询类型
 *  https://docs.zincsearch.com/api-es-compatible/search/types
 * </p>
 *
 * @author Alex
 * @version v1.0 2022/11/16
 */
public enum QueryType {

    // 自定义 bool 类型
    bool,
    //
    match_all,
    match,
    match_phrase,
    term,
    terms,
    query_string,
    prefix,
    wildcard,
    fuzzy,
    range,
    ids,
    exists

}
