package com.robotics.gui;

import com.robotics.decompose.*;
import com.robotics.decompose.Boustrophedon.CoverageBoustrophedonAlgorithm;
import com.robotics.decompose.Boustrophedon.Environment2;
import com.robotics.decompose.Boustrophedon.GroupTreeBoustrophedonAlgorithm;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException {

        File fs[] = new File("src/com/robotics/data").listFiles();

        String res = "";
        for (File f : fs) {
            res += run(f);
        }
        File result = new File("src/com/robotics/result");
        if(!result.exists()) result.mkdir();
        FileWriter fw = new FileWriter(result.getAbsoluteFile()+"/result.txt");
        fw.write(res);
        fw.close();
        System.out.println(res);
    }

    public static String run(File f) {
        if (!f.getName().endsWith(".txt")) return "";

        Environment e = new Environment(f.getAbsolutePath());
        Tree t = e.getTree();
        int B = -1;
        for (Cell c : e.getCells())
            if (B < c.getDistance()) B = c.getDistance();
        B = 2 * B + 22;

        GroupTreeAlgorithm group = new GroupTreeAlgorithm(t, B);
        ArrayList<Tree> A = group.getWorkingZone();

        CoverageAlgorithm algorithm = new CoverageAlgorithm(B, A);
        ArrayList<ArrayList<CoverageAlgorithm.Path>> paths = algorithm.coverage();

        ArrayList<CoverageAlgorithm.Path> P = paths.get(0);
        int length = 0;
        for (CoverageAlgorithm.Path p : P) {
            length += p.length();
        }


        Environment2 e2 = new Environment2(f.getAbsolutePath());
        com.robotics.decompose.Boustrophedon.Tree t2 = e2.getTreeBoustrophedon();
        GroupTreeBoustrophedonAlgorithm group2 = new GroupTreeBoustrophedonAlgorithm(t2, B);
        ArrayList<com.robotics.decompose.Boustrophedon.Tree> A2 = group2.getWorkingZone();
        CoverageBoustrophedonAlgorithm algorithm2 = new CoverageBoustrophedonAlgorithm(B, A2);
        ArrayList<ArrayList<CoverageBoustrophedonAlgorithm.Path>> paths2 = algorithm2.coverage();
        ArrayList<CoverageBoustrophedonAlgorithm.Path> P2 = paths2.get(0);
        int length2 = 0;
        for (CoverageBoustrophedonAlgorithm.Path p : P2) {
            length2 += p.length();
        }

        return "\n" + f.getName() + ", B = " + B + "\nAlgorithm 1, Length " + length + ", number of paths " + P.size() +
                "\nAlgorithm 2, Length " + length2 + ", number of paths " + P2.size() + "\n";
    }
}




