package org.mi.common.core.constant;

/**
 * @program: mi-community
 * @description: 认证客户端相关静态变量
 * @author: Micah
 * @create: 2020-10-24 23:25
 **/
public interface AuthClientConstant {
    /**
     * sys_oauth_client_details 表的字段，不包括client_id、client_secret
     */
    String CLIENT_FIELDS = "client_id, client_secret, resource_ids, scope, "
            + "authorized_grant_types, web_server_redirect_uri, authorities, access_token_validity, "
            + "refresh_token_validity, additional_information, autoapprove";

    /**
     * JdbcClientDetailsService 查询语句
     */
    String BASE_FIND_STATEMENT = "select " + CLIENT_FIELDS + " from sys_oauth_client_details";

    /**
     * 按条件client_id 查询
     */
    String DEFAULT_SELECT_STATEMENT = BASE_FIND_STATEMENT + " where client_id = ?";

    /**
     * 默认的查询语句,按照id排序
     */
    String DEFAULT_FIND_STATEMENT = BASE_FIND_STATEMENT + " order by client_id";

    String CLIENT_ID = "client_id";

    String CLIENT_SECRET = "client_secret";

    String GRANT_TYPE = "grant_type";

    String SCOPE = "scope";
}
