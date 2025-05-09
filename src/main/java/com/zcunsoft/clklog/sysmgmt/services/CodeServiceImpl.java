package com.zcunsoft.clklog.sysmgmt.services;

import com.zcunsoft.clklog.sysmgmt.models.enums.ErrorCode;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * 错误码的说明服务
 */
@Service
public class CodeServiceImpl implements ICodeService {

    private final MessageSource codeMessageSource;

    private final String messagePrefix = "code";
    private final Object[] emptyArgs = new Object[0];

    public CodeServiceImpl(MessageSource codeMessageSource) {
        this.codeMessageSource = codeMessageSource;
    }

    @Override
    public String getMessage(ErrorCode code, Object[] args) {
        String fullCode = String.format("%s.%s", messagePrefix, code.toString());
        Locale locale = LocaleContextHolder.getLocale();

        String message = codeMessageSource.getMessage(fullCode, args, locale);

        return message;
    }

    @Override
    public String getMessage(ErrorCode code) {
        return getMessage(code, emptyArgs);
    }
}
