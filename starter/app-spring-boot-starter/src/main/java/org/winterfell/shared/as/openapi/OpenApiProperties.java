package org.winterfell.shared.as.openapi;

import com.github.xiaoymin.knife4j.core.enums.OpenAPILanguageEnums;
import com.github.xiaoymin.knife4j.core.model.MarkdownProperty;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * openapi 增强配置
 * </p>
 *
 * @author Alex
 * @since 2025/9/25
 */
@Data
@ConfigurationProperties(prefix = "application.openapi")
public class OpenApiProperties {

    /**
     * 是否开启默认跨域
     */
    private boolean cors = false;

    /**
     * 是否开启BasicHttp验证
     */
    private HttpBasic basic;

    /**
     * 是否生产环境
     */
    private boolean production = false;

    /**
     * 个性化配置
     */
    private DocUiSetting setting;

    /**
     * 分组文档集合
     */
    private List<MarkdownProperty> documents;

    /**
     * openapi 的描述信息等
     */
    private Info info = null;

    /**
     * 需要认证(bearer)的路径配置 默认所有接口都不认证, 正则语法
     */
    private String[] authPaths = new String[0];

    @Data
    public static class Info {
        private String title = null;
        private String description = null;
        private String termsOfService = null;
        private Contact contact = null;
        private License license = null;
        private String version = null;
        private Map<String, Object> extensions = null;
        private String summary = null;
    }


    @Data
    public static class DocUiSetting {
        /**
         * Custom response HTTP status code after production environment screening(Knife4j.production=true)
         */
        private Integer customCode = 200;
        /**
         * i18n
         */
        private OpenAPILanguageEnums language = OpenAPILanguageEnums.ZH_CN;
        /**
         * Whether to display the Swagger Models function in the Ui Part.
         */
        private boolean enableSwaggerModels = true;
        /**
         * Rename Swagger model name,default is `Swagger Models`
         */
        private String swaggerModelName = "Swagger Models";

        /**
         * Whether to display the refresh variable button after each debug debugging bar, which is not displayed by default
         */
        private boolean enableReloadCacheParameter = false;

        /**
         * Whether the debug tab displays the afterScript function is enabled by default
         */
        private boolean enableAfterScript = true;

        /**
         * Whether to display the "document management" function in the Ui Part.
         */
        private boolean enableDocumentManage = true;
        /**
         * Whether to enable the version control of an interface in the interface. If it is enabled, the UI interface will have small blue dots after the backend changes
         */
        private boolean enableVersion = false;

        /**
         * Whether to enable request parameter cache
         */
        private boolean enableRequestCache = true;

        /**
         * For the interface request type of RequestMapping, if the parameter type is not specified, seven types of interface address parameters will be displayed by default if filtering is not performed. If this configuration is enabled, an interface address of post type will be displayed by default
         */
        private boolean enableFilterMultipartApis = false;

        /**
         * Filter Method type
         */
        private String enableFilterMultipartApiMethodType = "POST";

        /**
         * Enable host
         */
        private boolean enableHost = false;

        /**
         * HostAddress after enabling host
         */
        private String enableHostText = "";

        /**
         * Whether to enable dynamic request parameters
         */
        private boolean enableDynamicParameter = false;

        /**
         * Enable debug mode，default is true.
         */
        private boolean enableDebug = true;

        /**
         * Display bottom footer by default
         */
        private boolean enableFooter = true;
        /**
         * Customize footer
         */
        private boolean enableFooterCustom = false;

        /**
         * Custom footer content (support Markdown syntax)
         */
        private String footerCustomContent;

        /**
         * Show search box
         */
        private boolean enableSearch = true;

        /**
         * Whether to display the tab box of the original structure of OpenAPI, which is displayed by default
         */
        private boolean enableOpenApi = true;

        /**
         * Whether to enable home page custom configuration, false by default
         */
        private boolean enableHomeCustom = false;

        /**
         * Customize Markdown document content of home page
         */
        private String homeCustomLocation;

        /**
         * Customize Markdown document path of home page
         */
        private String homeCustomPath;
        /**
         * Whether to display the group drop-down box, the default is true (that is, display). In general, if it is a single group, you can set this property to false, that is, the group will not be displayed, so you don't need to select it.
         */
        private boolean enableGroup = true;

        /**
         * Whether to display the response status code bar
         * https://gitee.com/xiaoym/knife4j/issues/I3TE0V
         *
         * @since v4.0.0
         */
        private boolean enableResponseCode = true;
    }

    @Data
    public static class HttpBasic {

        /**
         * basic 是否开启, 默认 false
         */
        private boolean enabled = false;

        /**
         * basic 用户名
         */
        private String username;

        /**
         * basic 密码
         */
        private String password;

        /**
         * All configured urls need to be verified by basic，Only support Regex
         */
        private List<String> include;

    }
}