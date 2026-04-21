package com.agende_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProfissionalDashboardResponse {
  // dashboard de um profissional
  private long consultasHoje;
  private long pendentes;
  private long confirmadas;
  private long realizadas;
  private long canceladas;
}
