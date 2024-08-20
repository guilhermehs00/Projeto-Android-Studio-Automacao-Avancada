package com.guilherme.projetoautavancada;

import com.guilherme.mylibrary.Region;

public interface CallbackConsulta {
    void onResultado(boolean existeRegProx, boolean existeSubRegProx, boolean existeRestRegProx, Region region);
}
