package simpleimap;




public interface ItemFormatter {
    public abstract class ConfigPanel extends javax.swing.JPanel {
    }
    public Object formatData(DefaultItem item, ItemField field, DefaultItem column_config, Object input);
    public ConfigPanel getConfigPanel(DefaultItem column_config);
}