import csv
import os
import random

random.seed(42)

DIAS = 60
PASTA = "app/src/main/assets/dados"
os.makedirs(PASTA, exist_ok=True)

territorios = [
    ["jardim-flores", "Jardim das Flores", "BAIRRO", -23.55, -46.63],
    ["vila-esperanca", "Vila Esperança", "BAIRRO", -23.56, -46.65],
    ["parque-industrial", "Parque Industrial", "BAIRRO", -23.58, -46.61],
]

# id, tema, nome, unidade, limiar atencao, limiar critico
indicadores = [
    ["carga-pico", "ENERGIA", "Carga no horário de pico", "%", 80, 95],
    ["temp-max", "CLIMA", "Temperatura máxima", "°C", 32, 36],
    ["chuva-24h", "CLIMA", "Chuva acumulada em 24h", "mm", 30, 50],
]


def gerar_valor(territorio, indicador, dia):
    if indicador == "temp-max":
        valor = 26 + dia * 0.12 + random.uniform(-2, 2)

    elif indicador == "chuva-24h":
        if territorio == "vila-esperanca" and dia >= DIAS - 3:
            valor = random.uniform(35, 48)
        else:
            valor = random.uniform(0, 12)

    else:  # carga-pico
        if territorio == "jardim-flores":
            valor = 62 + dia * 0.3
            if dia > 40:
                valor = valor + (dia - 40) * 0.6
        else:
            valor = 60
        valor = valor + random.uniform(-3, 3)

    return round(valor, 1)


leituras = []

for t in territorios:
    for ind in indicadores:
        for dia in range(DIAS):
            # parque industrial fica sem dados nos ultimos 10 dias e com alguns buracos
            if t[0] == "parque-industrial":
                if dia >= DIAS - 10:
                    continue
                if random.randint(1, 100) <= 15:
                    continue

            # em vez da data, guarda quantos dias atras foi a leitura (0 = hoje).
            # o app transforma em data quando le o arquivo, entao a demonstracao
            # funciona em qualquer dia em que o app for aberto
            dias_atras = DIAS - 1 - dia
            valor = gerar_valor(t[0], ind[0], dia)
            leituras.append([ind[0], t[0], dias_atras, valor, "SINTETICA"])


def salvar_csv(nome, cabecalho, linhas):
    with open(PASTA + "/" + nome, "w", newline="", encoding="utf-8") as arquivo:
        escritor = csv.writer(arquivo)
        escritor.writerow(cabecalho)
        escritor.writerows(linhas)
    print(nome, "->", len(linhas), "linhas")


salvar_csv("territorios.csv", ["id", "nome", "tipo", "lat", "lon"], territorios)
salvar_csv("indicadores.csv", ["id", "tema", "nome", "unidade", "limiar_atencao", "limiar_critico"], indicadores)
salvar_csv("leituras.csv", ["indicador_id", "territorio_id", "dias_atras", "valor", "origem"], leituras)