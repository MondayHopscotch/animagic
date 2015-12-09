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
        AnimagicTextureData data2 = FileUtils.fromJson(AnimagicTextureData.class, json);
        String json2 = FileUtils.toJson(data2);
        Assert.assertEquals(json, json2);
        Assert.assertEquals(data.originX, data2.originX);
        Assert.assertEquals(data.originY, data2.originY);
    }

    @Test
    public void testSerializeAnimationData() {
        AnimagicAnimationData data = new AnimagicAnimationData();
        data.add(new AnimagicTextureData(1, 2));
        String json = FileUtils.toJson(data);
        AnimagicAnimationData data2 = FileUtils.fromJson(AnimagicAnimationData.class, json);
        String json2 = FileUtils.toJson(data2);
        Assert.assertEquals(json, json2);
        Assert.assertEquals(data.get(0).originX, data2.get(0).originX);
        Assert.assertEquals(data.get(0).originY, data2.get(0).originY);
    }
}
