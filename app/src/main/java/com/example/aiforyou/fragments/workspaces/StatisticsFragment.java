package com.example.aiforyou.fragments.workspaces;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aiforyou.R;
import com.example.aiforyou.mytools.CalculationPerCategory;
import com.example.aiforyou.mytools.MyTimer;

import com.example.aiforyou.mytools.statisticscanvas.ExcelReader;

import com.example.aiforyou.mytools.statisticscanvas.stats.CategoryQuantifier;
import com.example.aiforyou.mytools.statisticscanvas.StatisticsCanvas;
import com.example.aiforyou.mytools.statisticscanvas.stats.SegmentQuantifier;
import com.example.aiforyou.adapters.listadapters.StatsListAdapter;
import com.example.aiforyou.custom.ProjectDTO;
import com.example.aiforyou.dialogs.ErrorDialog;

import java.io.IOException;
import java.io.InputStream;

public class StatisticsFragment extends DialogFragment {
    public interface OnChangingRangeDisplayListener {
        void onChangeRangeDisplay(int n);
    }

    private StatisticsCanvas canvas;
    private RecyclerView statsList;

    private ExcelReader reader;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.statistics_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        canvas = view.findViewById(R.id.canvas);
        statsList = view.findViewById(R.id.statsList);

        statsList.setLayoutManager(new LinearLayoutManager(getContext()));

        new MyTimer((total) -> {
            try{
                draw();
            } catch (NullPointerException | IllegalStateException e) {
                openErrorDialog("Something goes wrong with the arguments!");
                dismiss();
            }
            catch (ArrayIndexOutOfBoundsException e) {
                openErrorDialog("Missing arguments!");
            }
        }).tick(600);
    }

    private void openErrorDialog(String msg) {
        Bundle bundle = new Bundle();
        bundle.putString("msg", msg);

        DialogFragment dialog = new ErrorDialog();
        dialog.setArguments(bundle);

        dialog.show(getParentFragmentManager(), "error");
    }

    private void draw() throws ArrayIndexOutOfBoundsException{
        ProjectDTO.ProjectType type = ProjectDTO.ProjectType.values()[getArguments().getInt("type", 0)];
        String[] args = getArguments().getStringArray("args");
        Uri uri = getArguments().getParcelable("uri");

        try {
            InputStream stream = requireActivity().getContentResolver().openInputStream(uri);
            reader = new ExcelReader(stream);
            CategoryQuantifier[] stats;

            Object[] data;
            switch (type) {
                case PIE:
                    stats = reader.getPieStats(args[1], args[0], 0);
                    data = new Object[stats.length];

                    System.arraycopy(stats, 0, data, 0, data.length);

                    statsList.setAdapter(new StatsListAdapter(data, StatsListAdapter.StatsType.PIE));
                    canvas.drawPieChart(reader, 0, args[0], args[1]);
                    break;
                case HIST1:
                    data = new Object[1];
                    data[0] = -Integer.parseInt(args[0]); // negative for masking first-time init of seekbar

                    statsList.setAdapter(new StatsListAdapter(data, StatsListAdapter.StatsType.HIST1)
                            .setOnChangingRangeDisplayListener((n) -> {
                                canvas.drawHistogramOnOneColumn(reader, 0, n, args[1], args[2].equals("true"));
                            }));

                    canvas.drawHistogramOnOneColumn(reader, 0, Integer.parseInt(args[0]), args[1], args[2].equals("true"));
                    break;
                case HIST2:
                    stats =  reader.splitUpWithCategory(
                            args[0],
                            args[1],
                            0,
                            CalculationPerCategory.values()[Integer.parseInt(args[2])]);

                    float total = 0;
                    for (CategoryQuantifier stat : stats) {
                        total += stat.value;
                    }

                    for (CategoryQuantifier stat : stats) {
                        stat.value /= total;
                    }

                    data = new Object[stats.length];
                    System.arraycopy(stats, 0, data, 0, data.length);
                    statsList.setAdapter(new StatsListAdapter(data, StatsListAdapter.StatsType.PIE));
                    canvas.drawHistogramOnTwoColumn(reader, 0, args[0], args[1],
                            CalculationPerCategory.values()[Integer.parseInt(args[2])], args[3].equals("true"));
                    break;
                case SCATTER:
                    String[] labels = reader.getScatterStats(reader, args[2], 0);
                    data = new Object[labels.length];
                    System.arraycopy(labels, 0, data, 0, data.length);

                    statsList.setAdapter(new StatsListAdapter(data, StatsListAdapter.StatsType.PIE));
                    canvas.scatter(reader, 0, args[0], args[1], args[2]);
                    break;
                default:
                    SegmentQuantifier[]
                            segmentStats = reader.getSegmentStats(args[0], args[1],  args[2], 0);

                    data = new Object[segmentStats.length];
                    System.arraycopy(segmentStats, 0, data, 0, data.length);

                    statsList.setAdapter(new StatsListAdapter(data, StatsListAdapter.StatsType.PIE));
                    canvas.drawSegments(reader, 0, args[0], args[1], args[2], Integer.parseInt(args[3]));
            }

            stream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        catch (IllegalArgumentException e) {
            ErrorDialog dialog = new ErrorDialog();

            Bundle bundle = new Bundle();
            bundle.putString("msg", "To many categories");
            dialog.setArguments(bundle);
            dialog.show(getParentFragmentManager(), "error");

            dismiss();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setStyle(STYLE_NO_TITLE, R.style.FullScreenDialogStyle);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        dialog.getWindow().setWindowAnimations(R.style.slideStyle);

        return dialog;
    }
}