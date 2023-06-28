package com.example.newfieldpasser.exception.comment;

import com.example.newfieldpasser.exception.comment.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Setter;

@Setter
@AllArgsConstructor
public class CommentException extends RuntimeException{

    private ErrorCode errorCode;
    private String message;

    public CommentException(ErrorCode err){
        this.errorCode = err;
    }




    @Override
    public String getMessage(){
        if(message == null){
            return "{" +
                    "\"state\":" + "\"" + errorCode.getStatus() + "\"" + "\n" +
                    "\t" + "\"message\":" + errorCode.getMessage() +
                    "}";
        }
        return "{" +
                "\"state\":" + "\"" + errorCode.getStatus() + "\"" + "\n" +
                "\t" + "\"message\":" + message +
                "}";
    }
}
