package fr.istic.mob.networkMP;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;

public class PathDeserializer implements JsonDeserializer<CustomPath>
{
    @Override
    public CustomPath deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        float xStart = jsonObject.get("xStart").getAsFloat();
        float yStart = jsonObject.get("yStart").getAsFloat();
        float xFinal = jsonObject.get("xFinal").getAsFloat();
        float yFinal = jsonObject.get("yFinal").getAsFloat();
        float xControl = jsonObject.get("xControl").getAsFloat();
        float yControl = jsonObject.get("yControl").getAsFloat();
        boolean isBent = jsonObject.get("isBent").getAsBoolean();
        int color = jsonObject.get("color").getAsInt();
        int strokeWidth = jsonObject.get("strokeWidth").getAsInt();
        CustomPath cp = new CustomPath(xStart,yStart,xFinal,yFinal,xControl,yControl,isBent,color,strokeWidth);
        cp.drawThisPath();
        return cp;
    }


}
