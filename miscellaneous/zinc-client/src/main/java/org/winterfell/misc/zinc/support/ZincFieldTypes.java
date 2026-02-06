package org.winterfell.misc.zinc.support;

import org.winterfell.misc.zinc.model.DocumentDateTimeField;
import org.winterfell.misc.zinc.model.DocumentField;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2023/4/21
 */
public enum ZincFieldTypes {

    // default type, this type will analyze string to some tokens
    text{
        @Override
        DocumentField toField() {
            return new DocumentField().setType(this.name());
        }
    },

    /**
     * use entire string as one token, when query it's just a term
     */
    keyword {
        @Override
        DocumentField toField() {
            return new DocumentField().setType(this.name());
        }
    },
    /**
     * like keyword, but the value just true or false
     * it can support term query, and terms aggregation
     */
    bool {
        @Override
        DocumentField toField() {
            return new DocumentField().setType(this.name());
        }
    },
    /**
     * `numberic` type, it supports range query
     * it can support term query, terms aggregation, range aggregation and all metrics aggregation, like min, max, avg, sum, count
     */
    numeric {
        @Override
        DocumentField toField() {
            return new DocumentField().setType(this.name());
        }
    },
    /**
     * date type, it supports range query, and date aggregations.
     */
    date {
        @Override
        DocumentField toField() {
            return new DocumentDateTimeField().setType(this.name());
        }
    };

    abstract DocumentField toField();
}
