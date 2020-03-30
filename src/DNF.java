import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

class DNF {
    private static final double ZERO = 0.0001;
    private Vector<Vector<Integer>> data;
    private String filePath;

    DNF() {
        chooseFile();
        try {
            initializeDataArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    void solve() {
        Vector<Vector<Integer>> org = new Vector<>(data);
        Vector<Integer> list = new Vector<>();
        for (int i = 0; i < org.get(0).size(); i++) {
            list.add(0);
        }

        List<Integer> positives = getPositiveCase();
        List<List<Integer>> h = new ArrayList<>();

        while (positives.size() > 0) {


            Vector<Vector<Integer>> copied = new Vector<>(data);
            List<Integer> r = new ArrayList<>();
            List<Integer> negatives = getNegativeCase(org);
            while (negatives.size() > 0) {
                if (r.size() == data.get(0).size()) {
                    JOptionPane.showMessageDialog(null, "Fail");
                    System.exit(0);
                }

                int best = getTheBestFeature(copied);
                r.add(best);
                List<Integer> cases = getCasesX(best, negatives);
                negatives.removeAll(cases);
                for (Integer aCase : cases) {
                    copied.set(aCase, list);
                }

            }

            h.add(r);
            List<Integer> coverage = getCoverage(r, positives);
            if (coverage.size() == 0) {
                JOptionPane.showMessageDialog(null, "Fail");
                System.exit(0);
            } else {
                positives.removeAll(coverage);
            }

            for (Integer integer : coverage) {
                data.set(integer, list);
            }
        }

        JOptionPane.showMessageDialog(null, "H: " + displayResult(h));
    }

    private List<Integer> getCoverage(List<Integer> r, List<Integer> positives) {
        List<Integer> coverage = new ArrayList<>();
        boolean check = true;
        for (Integer positive : positives) {
            for (Integer integer : r) {
                check = data.get(positive).get(integer) == 1;
            }
            if (check) {
                coverage.add(positive);
            }
        }

        return coverage;

    }

    private List<Integer> getCasesX(int best, List<Integer> negatives) {
        List<Integer> cases = new ArrayList<>();
        for (Integer negative : negatives) {
            if (data.get(negative).get(best) == 0) {
                cases.add(negative);
            }
        }

        return cases;
    }


    private List<Integer> getPositiveCase() {

        List<Integer> positives = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {

            if (data.get(i).get(data.get(0).size() - 1) == 1) {
                positives.add(i);
            }
        }
        return positives;
    }

    private List<Integer> getNegativeCase(Vector<Vector<Integer>> data) {
        List<Integer> negatives = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).get(data.get(0).size() - 1) == 0) {
                negatives.add(i);
            }
        }
        return negatives;
    }

    private List<String> loadFromFile() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));
        return bufferedReader.lines().collect(Collectors.toList());
    }

    void initializeDataArray() throws IOException {
        List<String> fromFile = loadFromFile();
        data = new Vector<>();

        for (String value : fromFile) {
            String[] strings = value.split(" ");
            Vector<Integer> row = new Vector<>();
            Arrays.stream(strings).forEach(s -> row.add(Integer.valueOf(s)));
            data.add(row);
        }
    }

    private List<Double> getIndicators(Vector<Vector<Integer>> data) {
        List<Double> indicators = new ArrayList<>();
        for (int i = 0; i < data.get(0).size() - 1; i++) {
            double numerator = 0;
            double denominator = 0;
            for (Vector<Integer> datum : data) {
                if (datum.get(i) == 1 && datum.get(data.get(0).size() - 1) == 1) {
                    numerator++;
                }
                if (datum.get(i) == 1 && datum.get(data.get(0).size() - 1) == 0) {
                    denominator++;
                }
            }
            if (denominator == 0) {
                denominator = ZERO;
            }
            indicators.add(numerator / denominator);
        }
        return indicators;
    }

    private int getTheBestFeature(Vector<Vector<Integer>> data) {
        List<Double> indicators = getIndicators(data);

        return indicators.indexOf(Collections.max(indicators));
    }

    private String displayResult(List<List<Integer>> hypothesis) {
        StringBuilder toDisplay = new StringBuilder();
        for (List<Integer> integers : hypothesis) {
            toDisplay.append("(");

            for (Integer integer : integers) {
                toDisplay.append('f')
                        .append(integer)
                        .append('^');
            }
            toDisplay.deleteCharAt(toDisplay.length() - 1);
            toDisplay.append(")").append('v');
        }

        toDisplay.deleteCharAt(toDisplay.length() - 1);

        return toDisplay.toString();
    }

    private void chooseFile() {
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setDialogTitle("Select an text file");
        jfc.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt");
        jfc.addChoosableFileFilter(filter);

        int returnValue = jfc.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            filePath = jfc.getSelectedFile().getPath();
        } else {
            System.exit(0);
        }
    }
}