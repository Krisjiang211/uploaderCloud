package com.xiangyueEducation.uploaderCloud.Utils;

/**
 * 统一返回结果状态信息类
 *
 */
public enum ResultCodeEnum {

    //IO操作失败以1开头
    IO_ERROR(100,"IO_ERROR"),
    FILE_DELETE_ERROR(101,"fileDeleteError"),

    SUCCESS(200,"success"),
    ID_ERROR(501,"identityError"),
    PASSWORD_ERROR(502,"passwordError"),
    NOTLOGIN(503,"notLogin"),
    ID_USED(504,"identityUsed"),
    REQUEST_FAIL(505,"requestFail"),
    USER_STOP(506,"userSTOP"),//账户被停用,所有服务不可用
    USER_LOGIN_LOCK(507,"userLoginLock"),//账户被禁止登录
    PROXY_USER_ERROR(508,"proxyUserError"),//代理用户错误

    //数据库查询错误以1开头
    QUERY_FAIL(101,"queryFail"),
    PAGE_OVERFLOW(102,"pageOverflow"),
    QUERY_EMPTY(103,"queryButGotNothing"),
    QUERY_HAVE_SAME(104,"haveNotAllowedSame"),
    //fileGroup的CodeEnum(以3开头)
    FILE_GROUP_TITLE_EXISTS_ERROR(301,"fileGroupTitleExistsError"),

    //危险操作4开头,444为最高级别危险
    SEVERE_HANDLER(444,"severeHandler(A Delete_action Maybe Invoked by A Stranger)"),
    //管理员以6开头
    ADMIN_LIMITED_AUTHORITY(601,"adminLimitedAuthority"),
    ADMIN_BAND(602,"adminBand");




    private Integer code;
    private String message;
    private ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
    public Integer getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }
}
