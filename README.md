# Radar do Bairro - Territory Intelligence Dashboard

App Android que reúne indicadores de energia e clima de bairros, prevê quando um indicador pode passar do limite e mostra o alerta com a justificativa. O app só recomenda: a decisão (acatar, adiar ou descartar) é sempre de uma pessoa e fica registrada.

Global Solution GS2026.2 - Cross-Platform Application Development - FIAP

## Integrantes

| Nome | RM |
|---|---|
| João Vitor Betiolli | 561835 |
| João Victor Caitano Tabuso | 562525 |

## O que o app faz

- Painel por bairro com indicadores (valor atual, variação em 7 dias e status) e alertas ativos
- Gráfico de tendência com histórico de 30 dias, previsão e limiares
- Alertas preditivos por regressão linear, sempre com justificativa, confiança, fonte e validade
- Registro da decisão humana com responsável, data e observação, e histórico de decisões
- Aviso de dados desatualizados (mais de 3 dias sem leitura): nesse caso o app não emite alerta
- Notificação local de alerta crítico, com tratamento de permissão negada (plano B)
- Funciona sem internet: dados no APK e banco local (Room)

## Tecnologias

- Kotlin 2.2.10 e Jetpack Compose (BOM 2026.02.01, Material 3)
- ViewModel + StateFlow, Navigation Compose 2.10.2
- Room 2.8.5 com KSP, SharedPreferences
- JUnit 4 para os testes do domínio
- minSdk 26, targetSdk 36, compileSdk 37

## Como executar

**Pré-requisitos:** Android Studio atualizado, SDK Platform da API 37 instalado (SDK Manager) e um emulador ou celular com Android 8.0 ou superior. Para testar a permissão de notificação, use Android 13 (API 33) ou superior. Testamos no emulador Pixel 6 com Android 16 (API 36.1).

1. Clone o repositório:
   ```
   git clone https://github.com/JoaoVitorBetiolli/TerritoryDashboard.git
   ```
2. Abra a pasta no Android Studio e espere o Gradle sincronizar.
3. Escolha o emulador e clique em Run.

### Gerar e instalar o APK

```
gradlew.bat assembleRelease        (Windows)
./gradlew assembleRelease          (Linux/Mac)
```

O APK fica em `app/build/outputs/apk/release/app-release.apk` (cerca de 1,5 MB, com R8 ligado). Para instalar com o emulador aberto:

```
adb install -r app/build/outputs/apk/release/app-release.apk
```

O release é assinado com a chave de debug só para poder ser instalado na entrega acadêmica.

### Rodar os testes

```
gradlew.bat testDebugUnitTest
```

São 5 testes do motor de previsão (regressão, alerta crítico, valores baixos, dados desatualizados e poucas leituras).

### Gerar os dados de novo

Os dados são sintéticos e saem do script `scripts/gerar_dados.py` (Python 3, sem bibliotecas extras). Rode na pasta raiz do projeto:

```
python scripts/gerar_dados.py
```

Ele recria os CSV em `app/src/main/assets/dados`. As leituras são guardadas como "dias atrás" (0 = hoje), e o app converte para data a partir do dia em que é aberto. Assim a demonstração funciona em qualquer data: o Jardim das Flores sempre tem alerta e o Parque Industrial sempre aparece com dados desatualizados.

## Arquitetura

Arquitetura em camadas (blueprint da disciplina), com MVVM dentro da interface.

```
com.example.territorydashboard
├── ui/        telas, componentes, tema e navegação (Compose + ViewModels)
├── domain/    modelos, motor de previsão, casos de uso e contratos (Kotlin puro)
├── data/      Room, leitura dos CSV e implementação dos repositórios
└── nativo/    notificação e permissão do Android
```

- O `domain` não importa nada do Android. Por isso o motor de previsão é testado com JUnit puro.
- A interface só conhece os contratos (`TerritorioRepositorio`, `PreferenciasRepositorio` e `Notificador`). Trocar a fonte sintética por uma fonte real é criar outra implementação, sem mexer no domínio.
- Cada tela tem um ViewModel com `StateFlow` e um `UiState` (Carregando, Sucesso, Vazio e Erro).
- As rotas de navegação levam só os ids (`indicador/{territorioId}/{indicadorId}`), nunca o objeto inteiro.

## Como o alerta é calculado

1. Pega as leituras dos últimos 14 dias do indicador.
2. Se a última leitura tem mais de 3 dias, ou se há menos de 7 leituras, não emite alerta.
3. Calcula a regressão linear e projeta o valor para daqui a 5 dias.
4. Compara o maior valor entre o atual e o previsto com os limiares: crítico, atenção ou informativo.
5. A confiança é metade pelo R² da reta e metade pela quantidade de dias com dado.

## Dados

Todos os dados são sintéticos (fictícios), criados pela equipe com seed fixa, sem dados pessoais. Os bairros (Jardim das Flores, Vila Esperança e Parque Industrial) são inventados. Cada um tem um comportamento para a demonstração: carga de energia subindo, chuva forte e dados desatualizados.

## Principais decisões

| Decisão | Alternativa descartada | Motivo |
|---|---|---|
| Android nativo com Kotlin e Compose | Flutter, React Native | Prazo, equipe e experiência no Android (ADR-001) |
| ViewModel + StateFlow | Estado só nas telas | O fluxo tem carga, banco, previsão e decisão |
| CSV no APK + Room | API remota | Uso offline e escopo da disciplina |
| Interface `Notificador` | Chamar a API do Android na tela | Testabilidade e manutenção |
| Injeção de dependência manual | Hilt | Mais simples para 5 telas |

A justificativa completa, os diagramas e as evidências de qualidade estão no documento PDF da entrega.

## Limitações e próximos passos

- Sem versão iOS nesta entrega.
- Regressão linear não prevê picos repentinos.
- Limiares iguais para todos os bairros.
- Próximos passos: fonte pública real (dados abertos de clima), limiares por bairro, localização para sugerir o bairro e medições em aparelho físico.

