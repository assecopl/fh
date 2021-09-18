package pl.fhframework.trees;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import pl.fhframework.core.FhFormException;
import pl.fhframework.core.io.FhResource;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.util.FileUtils;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.annotations.ElementPresentedOnTree;
import pl.fhframework.subsystems.ModuleRegistry;
import pl.fhframework.subsystems.Subsystem;
import pl.fhframework.subsystems.SubsystemManager;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public abstract class SubsystemElementsTree {

    protected static final String MENU_CONFIGURATION_FILE_PATH = "menu.xml";
    protected static final long MINIMAL_TIME_BETWEEN_SOURCE_REFRESH = 10000;


    /*
    private Predicate<ITreeElement> filter;

    public SubsystemElementsTree(Predicate<ITreeElement> filter) {
        this.filter = filter;
    }
    */

    public static List<ITreeElement> getStaticGroupsAndSubsystemsHierarchy(Class<? extends SubsystemElementsLiteTree> treeClass) {
        Map<String, List<ITreeElement>> groupdIdToChildrenList = new HashMap<>();
        // reading all use cases annotated as visible in tree

        for (Subsystem subsystem : SubsystemManager.getSubsystemInfos()) {
            if (subsystem.isStatic()) {
                for (Class<? extends IInitialUseCase> useCaseClass : subsystem.getStaticUseCaseInitializersList()) {
                    ElementPresentedOnTree displayedTreeElement = useCaseClass.getAnnotation(ElementPresentedOnTree.class);
                    if (displayedTreeElement != null) {
                        if (treeClass.equals(displayedTreeElement.tree())) {
                            List<ITreeElement> childrenList = groupdIdToChildrenList.get(displayedTreeElement.group().getName());
                            if (childrenList == null) {
                                childrenList = new ArrayList<>();
                                groupdIdToChildrenList.put(displayedTreeElement.group().getName(), childrenList);
                            }

                            ITreeElement newElement = new UseCaseInformation(useCaseClass);
                            //MetadataUC useCaseMetadata = new MetadataUC(useCaseClass.getName(), displayedTreeElement.label(), displayedTreeElement.image(), displayedTreeElement.coords(), displayedTreeElement.description(), false);
                            //childrenList.add(useCaseMetadata);
                            childrenList.add(newElement);
                        }
                    }
                }
            }
        }
//        for (Class<?> basePackage : SubsystemManager.instance.getBasicPackages()) {
//            NarzedziaRefleksji.getAnnotatedClasses(basePackage, PrezentowanyNaDrzewkuElement.class, ISubSystemElement.class).forEach(class_ -> {
//                PrezentowanyNaDrzewkuElement ppu = class_.getAnnotation(PrezentowanyNaDrzewkuElement.class);
//                if (treeClass.equals(ppu.drzewko())){
//                    List<IElementDrzewka> childrenList = groupdIdToChildrenList.get(ppu.grupa());
//                    if (childrenList==null){
//                        childrenList = new ArrayList<>();
//                        groupdIdToChildrenList.put(ppu.grupa(), childrenList);
//                    }
//                    MetadataUC useCaseMetadata = new MetadataUC(class_.getName(), ppu.label(), ppu.image(), ppu.coords(), ppu.description(), false);
//                    childrenList.add(useCaseMetadata);
//                }
//            });
//        }

        // read all groups
        Map<String, IGroupingTreeElement> groupIdToGroupInstance = new HashMap<>();
        for (Class<?> basePackage : SubsystemManager.getBasicPackages()) {
            ReflectionUtils.giveClassesTypeList(basePackage, IGroupingTreeElement.class).forEach(class_ -> {
                try {
                    IGroupingTreeElement groupInstance = class_.newInstance();
                    groupIdToGroupInstance.put(class_.getName(), groupInstance);
                    List<ITreeElement> childrenList = groupdIdToChildrenList.get(groupInstance.getContainerId());
                    if (childrenList == null) {
                        childrenList = new ArrayList<>();
                        groupdIdToChildrenList.put(groupInstance.getContainerId(), childrenList);
                    }
                    childrenList.add(groupInstance);
                } catch (InstantiationException | IllegalAccessException e) {
                    FhLogger.error(e);
                }
            });
        }

        List<ITreeElement> returnedRootsCollection = new ArrayList<>();
        // building hierarchy
        for (Map.Entry<String, List<ITreeElement>> entry : groupdIdToChildrenList.entrySet()) {
            IGroupingTreeElement group = groupIdToGroupInstance.get(entry.getKey());
            if (group != null) {
                for (ITreeElement treeElement : entry.getValue()) {
                    group.addSubelement(treeElement);
                }
            } else {
                for (ITreeElement treeElement : entry.getValue()) {
                    returnedRootsCollection.add(treeElement);
                }
            }
        }
        return returnedRootsCollection;
    }

    //finds grouping element by label, current level only, skip cloud exposed elements
    protected static IGroupingTreeElement findGroupByLabel(IGroupingTreeElement root, String label) {
        for (ITreeElement e : root.getSubelements()) {
            if (e.getCloudServerName() != null) {
                continue;
            }
            if (e instanceof IGroupingTreeElement && Objects.equals(e.getLabel(), label)) {
                return (IGroupingTreeElement) e;
            }
        }
        return null;
    }

    //merge 2 grouping elements by use case group label
    protected static void mergeGroups(IGroupingTreeElement root, IGroupingTreeElement other) {
        for (ITreeElement e : other.getSubelements()) {
            if (e.isGrouping()) {
                if (e.getCloudServerName() != null) {
                    root.getSubelements().add(e);
                } else {
                    IGroupingTreeElement current = findGroupByLabel(root, e.getLabel());
                    if (current == null) {
                        root.getSubelements().add(e);
                    } else {
                        mergeGroups(current, (IGroupingTreeElement) e);
                    }
                }
            } else {
                //normal use case element
                root.getSubelements().add(e);
            }
        }
    }

    public static List<TreeRoot> getDynamicGroupsAndSubsystemsHierarchy() {
        List<TreeRoot> roots = new ArrayList<>();
        ModuleRegistry.getLoadedModules().stream().filter(subsystem -> !subsystem.isStatic()).forEach(subsystem -> {
            TreeRoot treeRoot = getDynamicGroupsAndSubsystemsHierarchy(subsystem);
            roots.add(treeRoot);
        });

        return roots;
    }

    public static List<ITreeElement> mergeRoots(List<TreeRoot> roots) {
        TreeRoot main = new TreeRoot(null);
        for (TreeRoot treeRoot : roots) {
            mergeGroups(main, treeRoot);
        }
        return main.getSubelements();
    }

    public static TreeRoot getDynamicGroupsAndSubsystemsHierarchy(Subsystem subsystem) {
        TreeRoot treeRoot = new TreeRoot(subsystem);
        //first try generated menu.xml
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        FhResource menuResource = subsystem.getBasePath().resolve(MENU_CONFIGURATION_FILE_PATH);
        if (!menuResource.exists()) {
            //try menu.xml from jar
            try {
                menuResource = FhResource.get(
                        resolver.getResource(FileUtils.resolve(subsystem.getBaseClassPath(), MENU_CONFIGURATION_FILE_PATH).toExternalForm())
                );
            } catch (Exception e) {
                FhLogger.warn(
                        String.format("Cannot find %s file for subsystem %s", MENU_CONFIGURATION_FILE_PATH, subsystem.getLabel())
                );
                return treeRoot;
            }
        }

        treeRoot.setResourceLastChecked(System.currentTimeMillis());
        if (menuResource.exists()) {
            treeRoot = DynamicTreeElementsReader.instance.readObject(menuResource, treeRoot);
            treeRoot.setResourceTimestamp(FileUtils.getLastModified(menuResource).toEpochMilli());
        }

        return treeRoot;
    }

    /**
     * @see pl.fhframework.subsystems.DynamicSubsystem#requiresUpdate()
     */
    public static boolean requiresUpdate(TreeRoot treeRoot) {
        long currentTime = System.currentTimeMillis();
        long delta =  currentTime - treeRoot.getResourceLastChecked();
        if (delta > MINIMAL_TIME_BETWEEN_SOURCE_REFRESH) {
            treeRoot.setResourceLastChecked(currentTime);
            FhResource menuResource = treeRoot.getSubsystem().getBasePath().resolve(MENU_CONFIGURATION_FILE_PATH);
            if (menuResource.exists()) {
                long resourceModificationTime = FileUtils.getLastModified(menuResource).toEpochMilli();
                return resourceModificationTime != treeRoot.getResourceTimestamp();
            }
        }
        return false;
    }

    /**
     * Writes menu structure to xml file for given subsystem
     *
     * @param subsystem subsystem
     * @param xml xml menu content
     */
    public void exportMenu(Subsystem subsystem, String xml) {
        FhResource menuResource = subsystem.getBasePath().resolve(MENU_CONFIGURATION_FILE_PATH);

        Path targetPath = menuResource.getExternalPath();
        try {
            Files.createDirectories(targetPath.getParent());
            Files.write(targetPath, xml.getBytes(Charset.forName("UTF-8")));
        } catch (IOException e) {
            throw new FhFormException("Error writing: " + e.getMessage(), e);
        }

    }

    public abstract void onApplicationEvent(ContextRefreshedEvent event);
}