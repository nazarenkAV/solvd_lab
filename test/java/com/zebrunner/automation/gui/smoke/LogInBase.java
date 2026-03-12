package com.zebrunner.automation.gui.smoke;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import com.zebrunner.automation.api.iam.domain.AuthenticationData;
import com.zebrunner.automation.api.iam.domain.User;
import com.zebrunner.automation.api.iam.method.v1.Login;
import com.zebrunner.automation.api.iam.service.IAMService;
import com.zebrunner.automation.api.iam.service.IAMServiceImpl;
import com.zebrunner.automation.api.iam.service.UserService;
import com.zebrunner.automation.api.iam.service.UserServiceImpl;
import com.zebrunner.automation.api.launcher.domain.GitRepository;
import com.zebrunner.automation.api.launcher.domain.request.v1.CreateGitRepositoryRequest;
import com.zebrunner.automation.api.launcher.method.v1.CreateGitRepository;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.api.project.service.ProjectV1ServiceImpl;
import com.zebrunner.automation.api.reporting.service.ApiHelperService;
import com.zebrunner.automation.api.reporting.service.ApiHelperServiceImpl;
import com.zebrunner.automation.api.reporting.service.LauncherServiceImpl;
import com.zebrunner.automation.api.reporting.service.TestRunServiceAPIImplV1;
import com.zebrunner.automation.api.reporting.service.TestServiceV1Impl;
import com.zebrunner.automation.api.tcm.service.TcmService;
import com.zebrunner.automation.api.tcm.service.TcmServiceImpl;
import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.automation.config.GithubProperties;
import com.zebrunner.automation.config.UserProperties;
import com.zebrunner.automation.gui.iam.LoginPage;
import com.zebrunner.automation.legacy.IntegrationManager;
import com.zebrunner.automation.util.AuthenticationContext;
import com.zebrunner.automation.util.LocalStorageKey;
import com.zebrunner.automation.util.LocalStorageManager;
import com.zebrunner.carina.core.IAbstractTest;

public abstract class LogInBase implements IAbstractTest {

    public static final String PUBLIC_REPO_NAME = "dikazak/carina-demo";

    protected static Project project;
    protected static Long repositoryId;
    protected static User notProjectMember;

    protected static final IAMService iamService = new IAMServiceImpl();
    protected static final TcmService tcmService = new TcmServiceImpl();
    protected static final UserService userService = new UserServiceImpl();
    protected static final TestServiceV1Impl testService = new TestServiceV1Impl();
    protected static final ApiHelperService apiHelperService = new ApiHelperServiceImpl();
    protected static final LauncherServiceImpl launcherService = new LauncherServiceImpl();
    protected static final ProjectV1ServiceImpl projectV1Service = new ProjectV1ServiceImpl();
    protected static final TestRunServiceAPIImplV1 testRunService = new TestRunServiceAPIImplV1();

    @BeforeSuite
    public void onBeforeSuite() {
        projectV1Service.deleteAllProjects();

        project = projectV1Service.createProject();

        GitRepository gitRepository = this.prepareGitRepository();
        repositoryId = gitRepository.getId();

        notProjectMember = userService.addRandomUserToTenant();
    }

    @BeforeMethod
    public void onBeforeMethod() {
        WebDriver webDriver = this.getDriver();

        LoginPage loginPage = LoginPage.openPageDirectly(webDriver);
        loginPage.assertPageOpened();

        UserProperties.Admin admin = ConfigHelper.getUserProperties().getAdmin();
        AuthenticationData adminAuthData = Login.invoke(admin.getUsername(), admin.getPassword());

        LocalStorageManager localStorageManager = new LocalStorageManager(webDriver);
        localStorageManager.setItem(LocalStorageKey.ZBR_AUTH, adminAuthData);
    }

    private GitRepository prepareGitRepository() {
        GithubProperties githubProperties = ConfigHelper.getGithubProperties();
        String repositoryUrl = githubProperties.getPublicRepository().getUrl();

        String authToken = AuthenticationContext.getTenantAdminAuthToken();
        CreateGitRepositoryRequest request = CreateGitRepositoryRequest.ofGithub(
                repositoryUrl,
                githubProperties.getUsername(),
                githubProperties.getAccessToken()
        );

        return CreateGitRepository.invoke(project.getId(), authToken, request);
    }

    @AfterMethod
    public void onAfterMethod() {
        LocalStorageManager localStorageManager = new LocalStorageManager(this.getDriver());
        localStorageManager.removeItem(LocalStorageKey.ZBR_AUTH);
    }

    @AfterSuite
    public void onAfterSuite() {
        userService.deleteUserById(notProjectMember.getId());

        IntegrationManager.removeAllAddedIntegrations();
    }

}
