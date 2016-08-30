package ip.cynic.pullrecyclerview.bean.json;

import java.util.List;

/**
 * Created by cynic on 2016/6/2.
 */
public class GankIoJson {

    public boolean error;
    public List<BeautyResult> results;


    public class BeautyResult {
        public String createdAt;
        public String url;
    }

}
