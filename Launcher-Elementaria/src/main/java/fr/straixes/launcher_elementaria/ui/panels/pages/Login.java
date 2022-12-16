package fr.straixes.launcher_elementaria.ui.panels.pages;

import fr.litarvan.openauth.AuthPoints;
import fr.litarvan.openauth.AuthenticationException;
import fr.litarvan.openauth.Authenticator;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;
import fr.litarvan.openauth.model.AuthAgent;
import fr.litarvan.openauth.model.response.AuthResponse;
import fr.straixes.launcher_elementaria.Launcher;
import fr.straixes.launcher_elementaria.ui.PanelManager;
import fr.straixes.launcher_elementaria.ui.panel.Panel;
import fr.theshark34.openlauncherlib.minecraft.AuthInfos;
import fr.theshark34.openlauncherlib.util.Saver;
import javafx.geometry.HPos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class Login extends Panel {
    GridPane loginCard = new GridPane();
    Saver saver = Launcher.getInstance().getSaver();
    TextField userField = new TextField();
    PasswordField passwordField = new PasswordField();
    Label userErrorLabel = new Label();
    Label passwordErrorLabel = new Label();
    Button btnLogin = new Button("connexion");
    Button msLoginBtn = new Button();

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getStylesheetPath() {
        return "css/login.css";
    }

    @Override
    public void init(PanelManager panelManager) {
        super.init(panelManager);

        // Background
        this.layout.getStyleClass().add("login-layout");

        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setHalignment(HPos.LEFT);
        columnConstraints.setMinWidth(350);
        columnConstraints.setMaxWidth(350);
        this.layout.getColumnConstraints().addAll(columnConstraints, new ColumnConstraints());
        this.layout.add(loginCard, 0, 0);

        //background image
        GridPane bgImage = new GridPane();
        setCanTakeAllSize(bgImage);
        bgImage.getStyleClass().add("bg-image");
        this.layout.add(bgImage, 1, 0);

        //login card
        setCanTakeAllSize(this.layout);
        loginCard.getStyleClass().add("login-card");
        setLeft(loginCard);
        setCenterH(loginCard);
        setCenterV(loginCard);

        /*
         * Login sidebar
         */
        Label title = new Label("Elementaria");
        title.setFont(Font.font("Consolas", FontWeight.BOLD, FontPosture.REGULAR, 24f));
        title.getStyleClass().add("login-title");
        setCenterH(title);
        setCanTakeAllSize(title);
        setTop(title);
        title.setTextAlignment(TextAlignment.CENTER);
        title.setTranslateY(30d);
        loginCard.getChildren().add(title);

        //username/E-Mail
        setCanTakeAllSize(userField);
        setCenterV(userField);
        setCenterH(userField);
        userField.setPromptText("Adresse E-Mail");
        userField.setMaxWidth(310);
        userField.setTranslateY(-100d);
        userField.getStyleClass().add("login-input");
        userField.focusedProperty().addListener((a, oldValue, newValue) -> {
            if (!newValue) this.updateLoginBtnState(userField, userErrorLabel);
        });

        setCanTakeAllSize(userErrorLabel);
        setCenterV(userErrorLabel);
        setCenterH(userErrorLabel);
        userErrorLabel.setMaxWidth(290);
        userErrorLabel.setTranslateY(-70d);
        userErrorLabel.getStyleClass().add("login-error");
        userErrorLabel.setTextAlignment(TextAlignment.LEFT);

        setCanTakeAllSize(passwordField);
        setCenterV(passwordField);
        setCenterH(passwordField);
        passwordField.setPromptText("Mot de passe");
        passwordField.setMaxWidth(310);
        passwordField.setTranslateY(-35d);
        passwordField.getStyleClass().add("login-input");
        passwordField.focusedProperty().addListener((a, oldValue, newValue) -> {
            if (!newValue) this.updateLoginBtnState(passwordField, passwordErrorLabel);
        });

        //password Error
        setCanTakeAllSize(passwordErrorLabel);
        setCenterV(passwordErrorLabel);
        setCenterH(passwordErrorLabel);
        passwordErrorLabel.setMaxWidth(290);
        passwordErrorLabel.setTranslateY(0d);
        passwordErrorLabel.getStyleClass().add("login-error");
        passwordErrorLabel.setTextAlignment(TextAlignment.LEFT);

        //login button
        setCanTakeAllSize(btnLogin);
        setCenterV(btnLogin);
        setCenterH(btnLogin);
        btnLogin.setDisable(true);
        btnLogin.setMaxWidth(280);
        btnLogin.setTranslateY(40d);
        btnLogin.getStyleClass().add("login-log-btn");
        btnLogin.setOnMouseClicked(e -> {
            this.authenticate(userField.getText(), passwordField.getText());
        });

        Separator separator = new Separator();
        setCanTakeAllSize(separator);
        setCenterH(separator);
        setCenterV(separator);
        separator.getStyleClass().add("login-separator");
        separator.setMaxWidth(300);
        separator.setTranslateY(90d);

        //"loggin with" label
        Label loginWithLabel = new Label("Ou se connecter avec:".toUpperCase());
        setCanTakeAllSize(loginWithLabel);
        setCenterV(loginWithLabel);
        setCenterH(loginWithLabel);
        loginWithLabel.setFont(Font.font(loginWithLabel.getFont().getFamily(), FontWeight.BOLD, FontPosture.REGULAR, 14d));
        loginWithLabel.getStyleClass().add("login-with-label");
        loginWithLabel.setTranslateY(110d);
        loginWithLabel.setMaxWidth(280d);

        // Microsoft login button
        ImageView view = new ImageView(new Image("images/microsoft.png"));
        view.setPreserveRatio(true);
        view.setFitHeight(30d);
        setCanTakeAllSize(msLoginBtn);
        setCenterH(msLoginBtn);
        setCenterV(msLoginBtn);
        msLoginBtn.getStyleClass().add("ms-login-btn");
        msLoginBtn.setMaxWidth(300);
        msLoginBtn.setTranslateY(145d);
        msLoginBtn.setGraphic(view);
        msLoginBtn.setOnMouseClicked(e -> this.authenticateMS());


        loginCard.getChildren().addAll(userField, userErrorLabel, passwordField, passwordErrorLabel, btnLogin, separator, loginWithLabel, msLoginBtn);

    }

    public void updateLoginBtnState(TextField textField, Label errorLabel) {
        if (textField.getText().length() == 0) {
            errorLabel.setText("Le champs ne peut être vide");
        } else {
            errorLabel.setText("");
        }

        btnLogin.setDisable(!(userField.getText().length() > 0 && passwordField.getText().length() > 0));
    }

    public void authenticate(String user, String password) {
        Authenticator authenticator = new Authenticator(Authenticator.MOJANG_AUTH_URL, AuthPoints.NORMAL_AUTH_POINTS);

        try {
            AuthResponse response = authenticator.authenticate(AuthAgent.MINECRAFT, user, password, null);

            saver.set("accessToken", response.getAccessToken());
            saver.set("clientToken", response.getClientToken());
            saver.save();

            AuthInfos infos = new AuthInfos(
                    response.getSelectedProfile().getName(),
                    response.getAccessToken(),
                    response.getClientToken(),
                    response.getSelectedProfile().getId()
            );

            Launcher.getInstance().setAuthInfos(infos);
            this.logger.info("Hello " + infos.getUsername());
            panelManager.showPanel(new App());

        } catch (AuthenticationException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Une erreur est survenu lors de la connexion");
            alert.setContentText(e.getMessage());
            alert.show();
        }
    }
    public void authenticateMS() {
        MicrosoftAuthenticator authenticator = new MicrosoftAuthenticator();
        authenticator.loginWithAsyncWebview().whenComplete((response, error) -> {
            if (error != null) {
                Launcher.getInstance().getLogger().err(error.toString());
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setContentText(error.getMessage());
                alert.show();
                return;
            }

            saver.set("msAccessToken", response.getAccessToken());
            saver.set("msRefreshToken", response.getRefreshToken());
            saver.save();
            Launcher.getInstance().setAuthInfos(new AuthInfos(
                    response.getProfile().getName(),
                    response.getAccessToken(),
                    response.getProfile().getId()
            ));
            this.logger.info("Hello " + response.getProfile().getName());
        });
    }
}