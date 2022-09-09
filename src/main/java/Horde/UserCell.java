package Horde;

import javafx.scene.control.ListCell;

public class UserCell extends ListCell<User> {
    @Override
    protected void updateItem(Horde.User user, boolean empty) {
        super.updateItem(user, empty);
        if (empty) {
            setGraphic(null);
            setText(null);
        } else {
            this.setText(user.name);
        }
    }
}
