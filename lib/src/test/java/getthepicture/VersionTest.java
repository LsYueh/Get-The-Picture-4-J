package getthepicture;

import org.junit.jupiter.api.Test;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class VersionTest {

    /**
     * 測試 Informational Version 至少能讀到字串
     * (MinVer 主要版本來源)
     */
    @Test
    void informational_Should_Not_Be_Null_Or_Empty() {
        String version = Version.getInformational();
        assertFalse(version == null || version.isBlank(), "Informational version should not be null or empty.");
        System.out.println(version);
    }

    /**
     * 測試 File Version 至少能讀到字串
     */
    @Test
    void fileVersion_Should_Not_Be_Null_Or_Empty() {
        String version = Version.getFile();
        assertFalse(version == null || version.isBlank(), "File version should not be null or empty.");
        System.out.println(version);
    }

    /**
     * 額外測試：Informational Version 基本格式合理
     * (允許 SemVer + prerelease + metadata)
     */
    // @Test
    // void informational_Should_Look_Like_SemVer() {
    //     String version = Version.getInformational();
    //     // SemVer 寬鬆驗證
    //     // 支援：
    //     // 1.2.3
    //     // 1.2.3-alpha
    //     // 1.2.3-alpha.1+build
    //     String semverPattern = "^\\d+\\.\\d+\\.\\d+([\\-+].*)?$";
    //     assertTrue(Pattern.matches(semverPattern, version),
    //         "Version '" + version + "' does not appear to be a valid semantic version.");
    // }
}
