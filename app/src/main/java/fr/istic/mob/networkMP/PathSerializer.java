package fr.istic.mob.networkMP;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

/**
 * Class for serialize the path
 * @author Loan et Hafsa
 */
public class PathSerializer implements JsonSerializer<CustomPath> {

    @Override
    public JsonElement serialize(CustomPath src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonPath = new JsonObject();
        jsonPath.addProperty("xStart", src.getxStart());
        jsonPath.addProperty("yStart", src.getyStart());
        jsonPath.addProperty("xFinal", src.getxFinal());
        jsonPath.addProperty("yFinal", src.getyFinal());
        jsonPath.addProperty("color", src.getColor());
        jsonPath.addProperty("strokeWidth", src.getStrokeWidth());
        jsonPath.addProperty("isBent", src.isBent());
        jsonPath.addProperty("xControl", src.getxControl());
        jsonPath.addProperty("yControl", src.getyControl());
        return jsonPath;
    }

}
