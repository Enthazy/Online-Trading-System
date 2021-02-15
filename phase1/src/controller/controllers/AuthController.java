package controller.controllers;

import controller.presenters.*;
import controller.presenters.auth.*;
import persistence.exceptions.*;
import usecases.authentication.Authenticator;
import usecases.authentication.exceptions.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AuthController extends AbstractBaseController {

    protected Authenticator authenticator;

    /**
     * Initializes the class.
     * @param authenticator Authenticator
     * @param controllerPresenter ControllerPresenter
     * @param errorPresenter ErrorPresenter
     */
    public AuthController(Authenticator authenticator, ControllerPresenter controllerPresenter, ErrorPresenter errorPresenter) {
        super(controllerPresenter, errorPresenter);
        this.authenticator = authenticator;
    }


    private int loginAttempts = 0;

    /**
     * Registers a user based on command line input.
     */
    public void register() {

        RegisterIterator prompts = new RegisterIterator();
        List<String> registerInfo = new ArrayList<>();

        try {
            while (prompts.hasNext()) {
                controllerPresenter.display(prompts.next());
                registerInfo.add(br.readLine());
            }
            authenticator.register(registerInfo.get(0), registerInfo.get(1));
            this.controllerPresenter.get("registrationSuccessful");
        } catch (IOException | PersistenceException e) {
            errorPresenter.displayIOException();
        }  catch (DuplicatedUserNameException e) {
            errorPresenter.displayDuplicatedUserNameException();
            register();
        }
    }


    /**
     * Logs in a user based on command line input.
     */
    public void login() {

        LoginIterator prompts = new LoginIterator();
        List<String> loginInfo = new ArrayList<>();

        try{
            while(prompts.hasNext()) {
                System.out.println(prompts.next());
                loginInfo.add(br.readLine());
            }
            //Check authentication
            if(authenticator.authenticate(loginInfo.get(0), loginInfo.get(1))) {
                this.controllerPresenter.get("successfulLogin");
                this.loginAttempts = 0;
            }
        } catch (IOException | InvalidLoginCredentialsException e){
            errorPresenter.displayInvalidLoginCredentialsException();
            this.loginAttempts++;
            if(this.loginAttempts < 3) this.login();
            else {
                controllerPresenter.get("tooManyLoginAttempts");
                this.loginAttempts = 0;
                this.menu.navigateTo("root");
            }

        }

    }


    /**
     * Logs out from program.
     */
    public void logout() {
        authenticator.logout();
        this.menu.navigateTo("root");
    }


    /**
     * Exits program.
     */
    public void exit() {
        System.exit(0);
    }


}
