package com.zebrunner.automation.gui.tcm;

import com.zebrunner.automation.api.launcher.domain.vo.GitProvider;
import com.zebrunner.automation.config.ConfigHelper;
import com.zebrunner.carina.core.IAbstractTest;
import com.zebrunner.automation.api.iam.domain.AuthenticationData;
import com.zebrunner.automation.util.LocalStorageKey;
import com.zebrunner.automation.legacy.UsersEnum;
import com.zebrunner.automation.gui.iam.LoginPage;
import com.zebrunner.automation.api.project.domain.Project;
import com.zebrunner.automation.api.iam.domain.User;
import com.zebrunner.automation.api.tcm.domain.TestSuite;
import com.zebrunner.automation.api.reporting.service.ApiHelperService;
import com.zebrunner.automation.api.reporting.service.ApiHelperServiceImpl;
import com.zebrunner.automation.api.reporting.service.LauncherServiceImpl;
import com.zebrunner.automation.api.tcm.service.EnvironmentServiceImpl;
import com.zebrunner.automation.api.iam.service.IAMService;
import com.zebrunner.automation.api.iam.service.IAMServiceImpl;
import com.zebrunner.automation.api.project.service.ProjectV1ServiceImpl;
import com.zebrunner.automation.api.tcm.service.TcmService;
import com.zebrunner.automation.api.tcm.service.TcmServiceImpl;
import com.zebrunner.automation.api.reporting.service.TestRunServiceAPIImplV1;
import com.zebrunner.automation.api.reporting.service.TestServiceV1Impl;
import com.zebrunner.automation.api.iam.service.UserService;
import com.zebrunner.automation.api.iam.service.UserServiceImpl;
import com.zebrunner.automation.util.LocalStorageManager;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public abstract class TcmLogInBase implements IAbstractTest {

    protected final String PUBLIC_REPO_NAME = "dikazak/carina-demo";
    protected final User MAIN_ADMIN = UsersEnum.MAIN_ADMIN.getUser();
    public static final Map<String, AuthenticationData> LOCAL_STORAGE_DATA = new ConcurrentHashMap<>();

    protected final ProjectV1ServiceImpl projectService = new ProjectV1ServiceImpl();
    protected final IAMService iamService = new IAMServiceImpl();
    protected final TestRunServiceAPIImplV1 testRunService = new TestRunServiceAPIImplV1();
    protected final TestServiceV1Impl testService = new TestServiceV1Impl();
    protected final ApiHelperService apiHelperService = new ApiHelperServiceImpl();
    protected final LauncherServiceImpl launcherService = new LauncherServiceImpl();
    protected final EnvironmentServiceImpl environmentService = new EnvironmentServiceImpl();
    protected final UserService usersService = new UserServiceImpl();
    protected final TcmService tcmService = new TcmServiceImpl();

    private static Project project;
    private static Project emptyDataProject;
    private static Long repoId;
    private static TestSuite testSuite;

    @BeforeSuite
    public void cleanUpProjectsAndCreateProjectForTests() {
        projectService.deleteAllProjects();

        project = projectService.createProject();
        pause(5);// Wait for project creation and initialization to complete across all services
        testSuite = tcmService.createTestSuite(project.getId(), TestSuite.withRandomName());
        emptyDataProject = projectService.createProject();
        repoId = launcherService.addGitRepo(project.getId(),
                ConfigHelper.getGithubProperties().getUrl() + "/" + PUBLIC_REPO_NAME,
                ConfigHelper.getGithubProperties().getUsername(),
                ConfigHelper.getGithubProperties().getAccessToken(),
                GitProvider.GITHUB.toString()
        );
    }

    public AuthenticationData getLocalStorageData() {
        String testClassName = this.getClass().getSimpleName();

        if (LOCAL_STORAGE_DATA.containsKey(testClassName)) {
            return LOCAL_STORAGE_DATA.get(testClassName);
        } else {

            AuthenticationData authenticationData = iamService.login(MAIN_ADMIN);

            LOCAL_STORAGE_DATA.put(testClassName, authenticationData);
            return authenticationData;
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void setLocalStorage() {
        LoginPage loginPage = LoginPage.openPageDirectly(getDriver());
        loginPage.assertPageOpened();

        LocalStorageManager localStorageManager = new LocalStorageManager(getDriver());

        localStorageManager.setItem(LocalStorageKey.ZBR_AUTH, this.getLocalStorageData());
        pause(1);
    }

    @AfterClass(alwaysRun = true)
    public void clearLocalStorageData() {
        String testClassName = this.getClass().getSimpleName();
        LOCAL_STORAGE_DATA.remove(testClassName);
    }


    protected Project getCreatedProject() {
        return project.clone();
    }

    protected Project getEmptyProject() {
        return emptyDataProject.clone();
    }

    protected Long getCreatedRepoId() {
        return repoId;
    }

    protected TestSuite getCreatedTestSuite() {
        return testSuite;
    }

}