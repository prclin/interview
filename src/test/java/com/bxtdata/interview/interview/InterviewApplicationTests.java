package com.bxtdata.interview.interview;


import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.bxtdata.interview.interview.task1.entity.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@SpringBootTest
class InterviewApplicationTests {

    @Autowired
    ObjectMapper mapper;

    private List<TagTreeNode> readTagLibrary() throws IOException {

        List<TagTreeNode> tagTree = new LinkedList<>();
        String filePath = new ClassPathResource("/static/标签词库1026.xlsx").getFile().getAbsolutePath();
        EasyExcel.read(filePath, Tag.class, new PageReadListener<Tag>(dataList -> {
            for (Tag tag : dataList) {
                Set<String> names = new HashSet<>();
                names.add(tag.getName());
                List<TagTreeNode> children = insertNode(tagTree, new TagTreeNode(tag.getKey1(), new HashSet<>(names))).getChildren();
                List<TagTreeNode> children1 = insertNode(children, new TagTreeNode(tag.getKey2(), new HashSet<>(names))).getChildren();
                insertNode(children1, new TagTreeNode(tag.getKey3(), new HashSet<>(names)));
            }
        })).sheet().doRead();
        return tagTree;
    }

    private TagTreeNode insertNode(List<TagTreeNode> tree, TagTreeNode node) {
        for (TagTreeNode tagTreeNode : tree) {
            if (tagTreeNode.getKey().equals(node.getKey())) {
                tagTreeNode.addNames(node.getNames());
                return tagTreeNode;
            }
        }
        tree.add(node);
        return node;
    }

    private Set<String> findTag(List<TagTreeNode> tree, String storeName) {
        Set<String> strings = new HashSet<>();
        for (TagTreeNode node : tree) {
            if (!storeName.contains(node.getKey())) continue;
            List<TagTreeNode> children1 = node.getChildren();
            for (TagTreeNode child : children1) {
                if (!storeName.contains(child.getKey())) continue;
                List<TagTreeNode> children2 = node.getChildren();
                for (TagTreeNode child2 : children2) {
                    if (storeName.contains(child2.getKey())) strings.addAll(child2.getNames());
                }
                strings.addAll(child.getNames());
            }
            strings.addAll(node.getNames());
        }
        return strings;
    }

    @Test
    void TestCSV() {
        try {
            //加载tag
            List<TagTreeNode> tagTree = readTagLibrary();
            //输入文件
            File inFile = new ClassPathResource("/static/sample.csv").getFile();
            BufferedReader bufferedReader = new BufferedReader(new FileReader(inFile, StandardCharsets.UTF_8));
            org.apache.commons.csv.CSVParser csvParser = new org.apache.commons.csv.CSVParser(bufferedReader, CSVFormat.RFC4180);

            //输出文件
            File outFile = new File(ResourceUtils.getURL("classpath:static").getPath() + "/output.csv");
            if (outFile.exists()) {
                outFile.delete();
            }
            outFile.createNewFile();
            CSVFormat csvFormat = CSVFormat.RFC4180.withHeader("task_id", "storeId", "storeName", "tag");
            CSVPrinter csvPrinter = new CSVPrinter(new BufferedWriter(new FileWriter(outFile, StandardCharsets.UTF_8)), csvFormat);

            //去重集合
            HashSet<String> outSet = new HashSet<>();

            int j = 0;
            for (CSVRecord record : csvParser) {
                if (j == 0) {
                    j++;
                    continue;
                }
                Result result = mapper.readValue(record.get(4), Response.class).getResult();
                if (result == null) continue;

                List<BaseDatum> datums = result.getData();

                for (int i = 0; i < datums.size(); i++) {
                    BaseDatum datum = datums.get(i);
                    if (datum instanceof Datum1 datum1) {
                        Data1 data1 = datum1.getData();
                        if (outSet.contains(data1.getStoreId())) continue;
                        outSet.add(data1.getStoreId());
                        csvPrinter.print(record.get(1));
                        csvPrinter.print(data1.getStoreId());
                        String storeName = data1.getStoreName();
                        if (storeName != null) {
                            csvPrinter.print(storeName);
                            Set<String> tagSet = findTag(tagTree, storeName);
                            StringBuilder tags = new StringBuilder();
                            for (String s : tagSet) {
                                tags.append(s);
                                tags.append(" ");
                            }
                            csvPrinter.print(tags.toString());
                        }
                        csvPrinter.println();
                    } else if (datum instanceof Datum103 datum103) {
                        Data103 data103 = datum103.getData();
                        Resources resources = data103.getResources();
                        if (outSet.contains(resources.getStoreId())) continue;
                        outSet.add(data103.getStoreId());
                        csvPrinter.print(record.get(1));
                        csvPrinter.print(resources.getStoreId());
                        String storeName = resources.getStoreName();
                        if (storeName != null) {
                            csvPrinter.print(storeName);
                            Set<String> tagSet = findTag(tagTree, storeName);
                            StringBuilder tags = new StringBuilder();
                            for (String s : tagSet) {
                                tags.append(s);
                                tags.append(" ");
                            }
                            csvPrinter.print(tags.toString());
                        }
                        csvPrinter.println();
                    }

                }
            }
            csvPrinter.flush();
            csvPrinter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
