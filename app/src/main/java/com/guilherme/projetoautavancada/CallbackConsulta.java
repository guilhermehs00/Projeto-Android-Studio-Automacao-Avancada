package com.guilherme.projetoautavancada;

import com.guilherme.mylibrary.Region;

public interface CallbackConsulta {
    void onResultado(boolean existeRegProx, boolean existeSubRegProx, boolean existeRestRegProx, Region region, double Time_T2, double Time_T3);
}
