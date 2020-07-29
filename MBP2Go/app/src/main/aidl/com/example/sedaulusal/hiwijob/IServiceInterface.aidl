// IServiceInterface.aidl
package com.example.sedaulusal.hiwijob;

// Declare any non-default types here with import statements

interface IServiceInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

    String getSensorAccelerometer();

    String getSensorMagnetic();

    String getSensorName();

    String getSensorValue();



}
