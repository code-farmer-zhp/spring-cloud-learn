package com.peng.zhou;


import com.peng.zhou.extension.ExtensionLoader;
import org.junit.Test;


public class ExtensionLoaderTest {

    @Test
    public void getDefaultExtensionNameTest() throws Exception {
        ExtensionLoader<ExtSample> extensionLoader = ExtensionLoader.getExtensionLoader(ExtSample.class);
        String defaultExtensionName = extensionLoader.getDefaultExtensionName();
        System.out.println(defaultExtensionName);
        ExtSample defaultExtension = extensionLoader.getDefaultExtension();
        System.out.println(defaultExtension);
    }

    @Test
    public void getAdaptiveExtensionTest() throws Exception {
        ExtSample adaptiveExtension = ExtensionLoader.getExtensionLoader(ExtSample.class).getAdaptiveExtension();
    }
}
