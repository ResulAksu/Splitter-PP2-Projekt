package propra.splitter;

import static com.tngtech.archunit.library.Architectures.onionArchitecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@AnalyzeClasses(packagesOf = SplitterApplication.class)
public class ArchUnitTest {

  /**
   * Enforce Onion-Architecture
   */
  @ArchTest
  @SuppressFBWarnings("UrF")
  static final ArchRule onionArch = onionArchitecture()
      .withOptionalLayers(true)
      .domainModels("..domain.model..")
      .domainServices("..domain.service..")
      .applicationServices("..application..")
      .adapter("persistence", "..persistence..")
      .adapter("web", "..web..")
      .adapter("rest", "..rest..");
}
