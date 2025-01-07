package com.example.fridayEvening.util;

/**
 * Класс для хранения ошибок процессов
 */
public class ZeebeExceptionConstants {

    public static final String COMMAND_EXECUTION_EXCEPTION_CODE = "COMMAND_EXECUTION_EXCEPTION_CODE";
    public static final String COMPLETE_COMMAND_EXCEPTION_MESSAGE = "Could not execute complete command";
    public static final String THROW_ERROR_COMMAND_EXCEPTION_MESSAGE = "Could not execute throw error command";

    public static final String INTERNAL_SERVER_ERROR_EXCEPTION_CODE = "INTERNAL_SERVER_ERROR_EXCEPTION_CODE";
    public static final String INTERNAL_SERVER_ERROR_EXCEPTION_MESSAGE = "Caught internal server error";

    public static final String CRITICAL_ERROR_EXCEPTION_CODE = "CRITICAL_ERROR_EXCEPTION_CODE";
    public static final String CRITICAL_ERROR_EXCEPTION_MESSAGE = "Caught critical error";

    private ZeebeExceptionConstants() { }
}
