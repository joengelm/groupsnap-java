package groupsnap;

import org.json.JSONObject;

public interface JSONBinder<T> {

    /**
     * Populate the fields of this object from a JSONObject.
     *
     * @param obj the JSONObject to use as a data source
     * @return this object.
     */
    public T bind(JSONObject obj);

}
