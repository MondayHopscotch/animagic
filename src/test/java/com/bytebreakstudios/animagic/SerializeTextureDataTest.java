package com.bytebreakstudios.animagic;

import com.bytebreakstudios.animagic.texture.data.AnimagicAnimationData;
import com.bytebreakstudios.animagic.texture.data.AnimagicAtlasData;
import com.bytebreakstudios.animagic.texture.data.AnimagicTextureData;
import com.bytebreakstudios.animagic.utils.SerializationUtils;
import org.junit.Assert;
import org.junit.Test;

public class SerializeTextureDataTest {

    @Test
    public void testSerializeTextureData() {
        AnimagicTextureData data = new AnimagicTextureData(1, 2);
        String json = SerializationUtils.toJson(data);
        System.out.println("testSerializeTextureData 1: " + data);

        AnimagicTextureData data2 = SerializationUtils.fromJson(AnimagicTextureData.class, json);
        String json2 = SerializationUtils.toJson(data2);
        System.out.println("testSerializeTextureData 2: " + data2);

        Assert.assertEquals(json, json2);
        Assert.assertEquals(data.originX, data2.originX);
        Assert.assertEquals(data.originY, data2.originY);
    }

    @Test
    public void testSerializeAnimationData() {
        AnimagicAnimationData data = new AnimagicAnimationData();
        data.put(0, new AnimagicTextureData(1, 2));
        String json = SerializationUtils.toJson(data);
        System.out.println("testSerializeAnimationData 1: " + data);

        AnimagicAnimationData data2 = SerializationUtils.fromJson(AnimagicAnimationData.class, json);
        String json2 = SerializationUtils.toJson(data2);
        System.out.println("testSerializeAnimationData 2: " + data2);

        Assert.assertEquals(json, json2);
        Assert.assertEquals(data.get(0).originX, data2.get(0).originX);
        Assert.assertEquals(data.get(0).originY, data2.get(0).originY);
    }

    @Test
    public void testSerializeAnimationDataMultiple() {
        AnimagicAnimationData data = new AnimagicAnimationData();
        data.put(0, new AnimagicTextureData(1, 2));
        data.put(1, new AnimagicTextureData(5, 6));
        data.put(4, new AnimagicTextureData(8, 9));
        String json = SerializationUtils.toJson(data);
        System.out.println("testSerializeAnimationData 1: " + data);

        AnimagicAnimationData data2 = SerializationUtils.fromJson(AnimagicAnimationData.class, json);
        String json2 = SerializationUtils.toJson(data2);
        System.out.println("testSerializeAnimationData 2: " + data2);

        Assert.assertEquals(json, json2);
        Assert.assertEquals(data.get(0).originX, data2.get(0).originX);
        Assert.assertEquals(data.get(1).originX, data2.get(1).originX);
        Assert.assertEquals(data.get(4).originX, data2.get(4).originX);
    }

    @Test
    public void testSerializeAtlasData() {
        AnimagicAtlasData data = new AnimagicAtlasData();
        data.put("kick", new AnimagicAnimationData().putting(0, new AnimagicTextureData(1, 2)));
        String json = SerializationUtils.toJson(data);
        System.out.println("testSerializeAtlasData 1: " + data);

        AnimagicAtlasData data2 = SerializationUtils.fromJson(AnimagicAtlasData.class, json);
        String json2 = SerializationUtils.toJson(data2);
        System.out.println("testSerializeAtlasData 2: " + data2);

        Assert.assertEquals(json, json2);
        Assert.assertEquals(data.get("kick").get(0).originX, data2.get("kick").get(0).originX);
        Assert.assertEquals(data.get("kick").get(0).originY, data2.get("kick").get(0).originY);
    }

    @Test
    public void testSerializeAtlasDataMultiple() {
        AnimagicAtlasData data = new AnimagicAtlasData();
        data.put("kick", new AnimagicAnimationData()
                .putting(0, new AnimagicTextureData(1, 2))
                .putting(1, new AnimagicTextureData(5, 6))
                .putting(4, new AnimagicTextureData(8, 9)));
        String json = SerializationUtils.toJson(data);
        System.out.println("testSerializeAtlasData 1: " + data);

        AnimagicAtlasData data2 = SerializationUtils.fromJson(AnimagicAtlasData.class, json);
        String json2 = SerializationUtils.toJson(data2);
        System.out.println("testSerializeAtlasData 2: " + data2);

        Assert.assertEquals(json, json2);
        Assert.assertEquals(data.get("kick").get(0).originX, data2.get("kick").get(0).originX);
        Assert.assertEquals(data.get("kick").get(1).originX, data2.get("kick").get(1).originX);
        Assert.assertEquals(data.get("kick").get(4).originX, data2.get("kick").get(4).originX);
    }
}
