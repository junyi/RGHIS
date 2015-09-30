package sg.rghis.android.rss;

import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

public class EmptyElementConverter implements Converter<String> {
    @Override
    public String read(InputNode node) throws Exception {
        if (node.getValue() == null) {
            return "";
        }
        return node.getValue();
    }

    @Override
    public void write(OutputNode node, String value) throws Exception {
        node.setValue(value);
    }
}
