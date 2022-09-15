package ru.example.group.main.helper;

public class CpuCoresValidator {

    public static int getNumberOfCPUCores() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.availableProcessors();
    }

}
