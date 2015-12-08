package com.bytebreakstudios.animagic;

import com.bytebreakstudios.animagic.texture.AnimagicAnimationData;
import com.bytebreakstudios.animagic.texture.AnimagicTextureData;
import com.bytebreakstudios.animagic.utils.FileUtils;
import org.junit.Assert;
import org.junit.Test;

public class SerializeTextureData {

    @Test
    public void testSerializeTextureData() {
        AnimagicTextureData data = new AnimagicTextureData(1, 2);
        String json = FileUtils.toJson(data);
        System.out.println("JSON: " + json);
        AnimagicTextureData data2 = FileUtils.fromJson(AnimagicTextureData.class, json);
        String json2 = FileUtils.toJson(data2);
        Assert.assertEquals(json, json2);
    }

    @Test
    public void testSerializeAnimationData() {
        AnimagicAnimationData data = new AnimagicAnimationData();
        data.add(new AnimagicTextureData(1, 2));
        String json = FileUtils.toJson(data);
        System.out.println("JSON: " + json);
        AnimagicAnimationData data2 = FileUtils.fromJson(AnimagicAnimationData.class, json);
        String json2 = FileUtils.toJson(data2);
        Assert.assertEquals(json, json2);
    }
}
