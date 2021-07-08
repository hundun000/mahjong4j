package hundun.mahjong4j.extend.utls;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hundun
 * Created on 2020/08/19
 */
public class MultiLinesStringBuilder {
    List<StringBuilder> lineBuilders;
    int size;
    public MultiLinesStringBuilder(int numLine) {
        lineBuilders = new ArrayList<>(numLine);
        for (int i = 0; i < numLine; i++) {
            lineBuilders.add(new StringBuilder());
        }
        this.size = numLine;
    }
    
    public String mergeAsLines() {
        StringBuilder result = new StringBuilder();
        lineBuilders.forEach(lineBuilder -> {
            if (lineBuilder.length() > 0) {
                result.append(lineBuilder.toString()).append("\n");
            }
        });
        return result.toString();
    }
    
    public MultiLinesStringBuilder append(Object... texts) {
        int size = Math.min(this.size,texts.length);
        for (int i = 0; i < size; i++) {
            lineBuilders.get(i).append(texts[i]);
        }
        return this;
    }
    
}
