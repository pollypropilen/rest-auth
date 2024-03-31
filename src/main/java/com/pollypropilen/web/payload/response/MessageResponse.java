package com.pollypropilen.web.payload.response;

import com.pollypropilen.web.payload.misc.Const;
import com.pollypropilen.web.payload.misc.ObjectFormat;
import com.pollypropilen.web.payload.misc.ObjectType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MessageResponse {
    private String code;
    private String message;
    private ObjectType object;
    private ObjectFormat type;
    private Object data;

    public static MessageResponse OK(String message, ObjectType object) {
        return new MessageResponse(Const.OK, message, object, null, null);
    }

    public static MessageResponse ERROR(String message, ObjectType object) {
        return new MessageResponse(Const.ERROR, message, object, null, null);
    }

    public static MessageResponse DATA(String message, ObjectType object, ObjectFormat type, Object data) {
        return new MessageResponse(Const.OK, message, object, type, data);
    }
}