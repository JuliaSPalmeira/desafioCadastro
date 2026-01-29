import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ClientInfoStatus;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.stream.Collectors;

enum TipoPet {
    CACHORRO, GATO;
}

enum SexoPet {
    MACHO, FEMEA;
}

class pet {
    public static final String NAO_INFORMADO = "NAO_INFORMADO";
    String nome, rua, numero, cidade, bairro, idade, peso, raca;
    TipoPet tipo;
    SexoPet sexo;

    public pet(String nome, TipoPet tipo, SexoPet sexo, String rua, String numero, String cidade, String bairro, String idade, String peso, String raca) {
        this.nome = nome;
        this.tipo = tipo;
        this.sexo = sexo;
        this.rua = rua;
        this.numero = numero;
        this.cidade = cidade;
        this.bairro = bairro;
        this.idade = idade;
        this.peso = peso;
        this.raca = raca;
    }
}

public class menuInicial {
    public static void main(String[] args) {
        Path caminho = Paths.get("formulario.txt");
        List<String> linhas = null;
        List<pet> bancoDeDados = new ArrayList<>();
        try {
            linhas = Files.readAllLines(caminho);
            for (String linha : linhas) {
                System.out.println(linha);
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o Arquivo: " + e.getMessage());
        }


        File pasta = new File("petsCadastrados");
        if (pasta.exists() && pasta.isDirectory()) {
            File[] arquivos = pasta.listFiles((dir, nome) -> nome.endsWith(".txt"));
            if (arquivos != null) {
                for (File arquivo : arquivos) {
                    try {
                        List<String> dados = Files.readAllLines(arquivo.toPath());
                        if (dados.size() >= 6) {
                            String nome = dados.get(0);

                            // Tratamento seguro para TipoPet
                            TipoPet tipo = dados.get(1).equalsIgnoreCase("GATO") ? TipoPet.GATO : TipoPet.CACHORRO;

                            // Tratamento seguro para SexoPet (EVITA O ERRO DO cMEA)
                            SexoPet sexo = dados.get(2).equalsIgnoreCase("MACHO") ? SexoPet.MACHO : SexoPet.FEMEA;

                            String[] partesEnd = dados.get(3).split(",");
                            String rua = (partesEnd.length > 0) ? partesEnd[0] : "";
                            String numero = (partesEnd.length > 1) ? partesEnd[1] : "";
                            String cidade = (partesEnd.length > 2) ? partesEnd[2] : "";
                            String bairro = (partesEnd.length > 3) ? partesEnd[3] : "";

                            String idade = dados.get(4);
                            String peso = dados.get(5);
                            String raca = (dados.size() > 6) ? dados.get(6) : "NAO_INFORMADO";

                            bancoDeDados.add(new pet(nome, tipo, sexo, rua, numero, cidade, bairro, idade, peso, raca));
                        }
                    } catch (Exception e) {
                        System.err.println("Erro crítico no arquivo " + arquivo.getName() + ": " + e.getMessage());
                    }
                }
            }
        }
        System.out.println("SISTEMA: " + bancoDeDados.size() + " pets carregados com sucesso.");

        imprimirMenu();
        Scanner scanner = new Scanner(System.in);
        int opcao = 0;
        while (opcao != 6) {
            if (scanner.hasNextInt()) {
                opcao = scanner.nextInt();

                switch (opcao) {
                    case 1:
                        System.out.println("Iniciando cadastro");
                        scanner.nextLine();

                        System.out.print(linhas.get(0) + " ");
                        String nomeCompleto = scanner.nextLine().trim();

                        try {
                            if (nomeCompleto.isEmpty()) {
                                nomeCompleto = pet.NAO_INFORMADO;
                            } else if (!nomeCompleto.contains(" ") || nomeCompleto.split(" ").length < 2 || !nomeCompleto.matches("[a-zA-ZÀ-ÿ\\s]+")) {
                                throw new IllegalArgumentException("Erro:  Nome inválido! Digite nome e sobrenome usando apenas letras.");
                            }

                            System.out.print(linhas.get(1) + " ");
                            TipoPet tipo = TipoPet.valueOf(scanner.nextLine().trim().toUpperCase());


                            System.out.print(linhas.get(2) + "(MACHO/FEMEA): ");
                            SexoPet sexo = SexoPet.valueOf(scanner.nextLine().trim().toUpperCase());

                            System.out.println(linhas.get(3) + " ");

                            System.out.println("Rua: ");
                            String rua = scanner.nextLine();

                            System.out.println("Número da casa: ");
                            String numero = scanner.nextLine().trim();
                            String numeroParaSalvar;

                            if (numero.isEmpty()) {
                                numeroParaSalvar = pet.NAO_INFORMADO;
                            } else {
                                if (!numero.matches("[0-9]+")) {
                                    throw new IllegalArgumentException("Erro: O número da casa deve conter apenas algarismos!");
                                }
                                numeroParaSalvar = numero;
                            }


                            System.out.println("Cidade: ");
                            String cidade = scanner.nextLine();

                            System.out.println("Bairro: ");
                            String bairro = scanner.nextLine();


                            System.out.print(linhas.get(4) + " ");
                            String idadeDigitada = scanner.nextLine().trim().replace(",", ".");
                            String idadeParaSalvar;

                            if (idadeDigitada.isEmpty()) {
                                idadeParaSalvar = pet.NAO_INFORMADO;
                            } else {
                                if (!idadeDigitada.matches("[0-9.,]+")) {
                                    throw new IllegalArgumentException("Erro: digite apenas números");
                                }
                                double valorIdade = Double.parseDouble(idadeDigitada);

                                if (valorIdade < 1 && valorIdade > 0 && idadeDigitada.contains(".")) {
                                    valorIdade = (valorIdade * 10) / 12.0;
                                    System.out.println("Idade convertida de meses para anos: " + String.format("%.2f", valorIdade));
                                }

                                if (valorIdade > 20) {
                                    throw new IllegalArgumentException("Erro: A idade do pet não pode ser maior que 20 anos!");
                                }
                                idadeParaSalvar = String.valueOf(valorIdade);
                            }
                            System.out.print(linhas.get(5) + " ");
                            String pesoDigitado = scanner.nextLine().trim().replace(",", ".");
                            String pesoFinal;

                            if (pesoDigitado.isEmpty()) {
                                pesoFinal = pet.NAO_INFORMADO;
                            } else {
                                if (!pesoDigitado.matches("[0-9.,]+")) {
                                    throw new IllegalArgumentException("Erro: digite apenas números");
                                }
                                double valorPeso = Double.parseDouble(pesoDigitado);
                                if (valorPeso < 0.5 || valorPeso > 60) {
                                    throw new IllegalArgumentException("Erro: o peso deve ser entre 0.5kg e 60kg.");
                                }
                                pesoFinal = String.valueOf(valorPeso);
                            }

                            System.out.print(linhas.get(6) + " ");
                            String raca = scanner.nextLine().trim();

                            if (raca.isEmpty()) {
                                raca = pet.NAO_INFORMADO;
                            } else if (!raca.matches("[a-zA-ZÀ-ÿ\\s]+")) {
                                throw new IllegalArgumentException("Erro: A raça deve conter apenas letras e espaços, sem números ou símbolos!");
                            }

                            pet novopet = new pet(nomeCompleto, tipo, sexo, rua, numeroParaSalvar, cidade, bairro, idadeParaSalvar, pesoFinal, raca);
                            bancoDeDados.add(novopet);

                            java.time.LocalDateTime agora = java.time.LocalDateTime.now();//acessa o relógio do sistema e captura a data e hora exata do momento do cadastro (Ano, Mês, Dia, Hora, Minuto, Segundo e Nanosegundo).
                            java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmm"); //Define como a data deve ser escrita no texto.'T': O Java trata letras dentro de aspas simples como texto fixo, inserindo o "T" para separar a data da hora

                            String dataHora = agora.format(fmt);//Você precisa transformar o 'agora' em texto usando o formato

                            String nomeFormatado = nomeCompleto.replace(" ", "").toUpperCase();
                            String nomeDoArquivo = dataHora + "-" + nomeFormatado + ".txt";


                            String enderecoCompleto = rua + "," + numeroParaSalvar + "," + cidade + "," + bairro;

                            List<String> respostas = new ArrayList<>();
                            respostas.add(nomeCompleto);
                            respostas.add(tipo.name());
                            respostas.add(sexo.name());
                            respostas.add(enderecoCompleto);
                            respostas.add(idadeParaSalvar);
                            respostas.add(pesoFinal);
                            respostas.add(raca);

                            File pasta1 = new File("petsCadastrados");
                            if (!pasta.exists()) {
                                pasta.mkdirs();//Cria a pasta caso ela não exista
                            }

                            File arquivoFinal = new File(pasta, nomeDoArquivo);
                            try (FileWriter writer = new FileWriter(arquivoFinal)) {
                                for (int i = 0; i < respostas.size(); i++) {
                                    int num = i + 1;
                                    writer.write(num + "-" + respostas.get(i) + System.lineSeparator());
                                }
                                System.out.println("Arquivo gerado com sucesso: " + nomeDoArquivo);
                            } catch (IOException e) {
                                System.out.println("Erro ao salvar o arquivo: " + e.getMessage());
                            }

                            System.out.println("\n✅ Pet cadastrado com sucesso!");
                            imprimirMenu();
                        } catch (IllegalArgumentException e) {
                            System.err.println(e.getMessage());
                            System.out.println("O cadastro foi cancelado. tente novamente");
                            imprimirMenu();
                        }
                        break;
                    case 2:
                        System.out.println("--- ALTERAÇÃO DOS DADOS DE UM PET ---");

                        System.out.println("Primeiro, informe o Tipo (CACHORRO ou GATO): "); //lógica de busca por filtro
                        scanner.nextLine();
                        String tipoAlt = scanner.nextLine().trim().toUpperCase();

                        if (!tipoAlt.equals("CACHORRO") && !tipoAlt.equals("GATO")) {
                            System.out.println("⚠️ Tipo inválido!");
                            break;
                        }

                        //filtra e exibe a lista para seleção
                        List<pet> filtradosAlt = bancoDeDados.stream().filter(p -> p.tipo.name().equalsIgnoreCase(tipoAlt)).collect(Collectors.toList());


                        if (filtradosAlt.isEmpty()) {
                            System.out.println("Nenhum pet encontrado");
                            break;
                        }

                        for (int i = 0; i < filtradosAlt.size(); i++) {
                            pet p = filtradosAlt.get(i);
                            System.out.printf("%d. %s - %s - %s - %s - %s - %s - %s - %s - %s%n",
                                    (i + 1), p.nome, p.raca, p.cidade, p.tipo, p.peso, p.bairro, p.idade, p.rua, p.sexo, p.numero
                            );
                        }

                        System.out.println("\nDigite o número do pet que deseja alterar: ");
                        int escolha = scanner.nextInt();
                        scanner.nextLine();

                        if (escolha < 1 || escolha > filtradosAlt.size()) {
                            System.out.println("⚠️ Opção inválida!");
                            break;
                        }

                        //ref do pet alterado
                        pet petSelecionado = filtradosAlt.get(escolha - 1);

                        //solicitação de novos dados(exceto tipo e sexo)
                        System.out.println("--- Informe os novos dados para " + petSelecionado.nome + "---");

                        System.out.println("Novo Nome:");
                        petSelecionado.nome = scanner.nextLine();

                        System.out.println("Nova Idade:");
                        petSelecionado.idade = scanner.nextLine();

                        System.out.println("Novo Peso:");
                        petSelecionado.peso = scanner.nextLine();

                        System.out.println("Nova Raça:");
                        petSelecionado.raca = scanner.nextLine();

                        System.out.println("Nova Rua:");
                        petSelecionado.rua = scanner.nextLine();

                        System.out.println("Novo Bairro:");
                        petSelecionado.bairro = scanner.nextLine();

                        System.out.println("Nova Cidade:");
                        petSelecionado.cidade = scanner.nextLine();

                        System.out.println("Novo Número:");
                        petSelecionado.numero = scanner.nextLine();

                        System.out.println("✅ Dados alterados com sucesso!");

                        break;
                    case 3:
                        System.out.println("Deletar os dados do pet cadastrado");
                        scanner.nextLine();

                        while (true) {
                            System.out.println("Primeiro, informe o Tipo (CACHORRO ou GATO): "); //lógica de busca por filtro
                            String tipoDel = scanner.nextLine().trim().toUpperCase();

                            if (!tipoDel.equals("CACHORRO") && !tipoDel.equals("GATO")) {
                                System.out.println("⚠️ Tipo inválido!");
                                continue; //exibe o menu de busca novamente
                            }

                            List<pet> filtradosDel = bancoDeDados.stream()
                                    .filter(p -> p.tipo.name().equalsIgnoreCase(tipoDel))
                                    .collect(Collectors.toList());

                            if (filtradosDel.isEmpty()) {
                                System.out.println("Nenhum pet encontrado");
                                break;
                            }

                            //exibe a lista para escolha
                            for (int i = 0; i < filtradosDel.size(); i++) {
                                System.out.println(i + " - " + filtradosDel.get(i).nome);
                            }

                            System.out.println("Escolha o número do pet que deseja deletar");
                            if (scanner.hasNextInt()) {
                                int escolhaDel = scanner.nextInt();
                                scanner.nextLine(); //limpar buffer

                                if (escolhaDel >= 0 && escolhaDel < filtradosDel.size()) {
                                    pet petAlvo = filtradosDel.get(escolhaDel);

                                    System.out.println("Confirmar exclusão de " + petAlvo.nome + "? (SIM/NÃO)");
                                    String confirma = scanner.nextLine().trim().toUpperCase();

                                    if (confirma.equals("SIM")) {
                                        System.out.println("tamanho antes: " + bancoDeDados.size());
                                        bancoDeDados.remove(petAlvo);
                                        System.out.println("tamanho depois: " + bancoDeDados.size());

                                        try {
                                            File pastaDel = new File("petsCadastrados");
                                            File[] arquivos = pastaDel.listFiles();

                                            if (arquivos != null) {
                                                String nomeBuscarArquivo = petAlvo.nome.replace("1-", "").trim().toUpperCase();
                                                for (File f : arquivos) {
                                                    if (f.getName().contains(nomeBuscarArquivo)) {
                                                        if (f.delete()) {
                                                            System.out.println("Arquivo " + f.getName() + " removido fisicamente.");
                                                        }
                                                    }
                                                }
                                            }
                                        }catch (SecurityException e){
                                            System.out.println("Erro de permissão ao acessar/deletar arquivos: " + e.getMessage());
                                        }catch (Exception e){
                                            System.out.println("Ocorreu um erro inesperado: "+ e.getMessage());
                                        }
                                        System.out.println("✅ Pet deletado com sucesso!");//remove do banco principal
                                    } else {
                                        System.out.println("Operação candelada.");
                                    }
                                    break; // sai do loop após finalizar
                                } else {
                                    System.out.println("\uFE0F Número inválido!");
                                    //o while fara exibir a lista nivamente
                                }
                            }
                        }
                        imprimirMenu();
                        break;
                    case 4:
                        System.out.println("Listar todos os pets cadastrados");
                        if (bancoDeDados.isEmpty()) {
                            System.out.println("Nenhum pet encontrado no sistema.");
                        } else {
                            // Percorre a lista que você carregou no início do programa
                            for (int i = 0; i < bancoDeDados.size(); i++) {
                                pet p = bancoDeDados.get(i);
                                System.out.println((i + 1) + ". " + p.nome + " [" + p.tipo + "]");
                                System.out.println("   Raça: " + p.raca + " | Idade: " + p.idade + " anos");
                                System.out.println("   Sexo: " + p.sexo + " | Peso: " + p.peso + "kg");
                                System.out.println("   Endereço: " + p.rua + ", " + p.numero + " - " + p.bairro + "/" + p.cidade);
                                System.out.println("---------------------------------");
                            }
                        }
                        break;
                    case 5:
                        System.out.println(" \uD83D\uDD0D --- BUSCA DE PETS --- \uD83D\uDD0D");
                        scanner.nextLine();//limpa o buffer


                        System.out.println("Primeiro, informe o Tipo (CACHORRO ou GATO): ");
                        String tipoEntrada = scanner.nextLine().trim().toUpperCase();

                        if (!tipoEntrada.equals("CACHORRO") && !tipoEntrada.equals("GATO")) {
                            System.out.println("⚠️ Tipo inválido! Digite exatamente CACHORRO ou GATO.");
                            break;
                        }

                        List<pet> filtrados = bancoDeDados.stream().filter(p -> p.tipo.name().trim().equalsIgnoreCase(tipoEntrada.trim())).collect(Collectors.toList());

                        System.out.println("Escolha até 2 critérios extras separados por vírgula (ex: 1,3):");
                        System.out.println("1.Nome | 2.Sexo | 3.Idade | 4.Peso | 5.Raça | 6.Endereço");
                        String[] escolhas = scanner.nextLine().split(",");

                        for (int i = 0; i < Math.min(escolhas.length, 2); i++) {
                            String criterio = escolhas[i].trim();

                            switch (criterio) {
                                case "1": //nome e sobrenome
                                    System.out.println("Digite o nome ou sobrenome");
                                    String buscarNome = normalizar(scanner.nextLine());
                                    filtrados = filtrados.stream().filter(p -> normalizar(p.nome).contains(buscarNome)).collect(Collectors.toList());
                                    break;
                                case "2": // Sexo (Exato)
                                    System.out.print("Digite o Sexo (MACHO/FEMEA): ");
                                    String buscaSexo = scanner.nextLine().trim();
                                    filtrados = filtrados.stream()
                                            .filter(p -> p.sexo.name().equalsIgnoreCase(buscaSexo))
                                            .collect(Collectors.toList());
                                    break;
                                case "3": // Idade (Exato)
                                    System.out.print("Digite a Idade: ");
                                    String buscaIdade = scanner.nextLine().trim();
                                    filtrados = filtrados.stream()
                                            .filter(p -> p.idade.equals(buscaIdade))
                                            .collect(Collectors.toList());
                                    break;
                                case "4": // Peso (Exato)
                                    System.out.print("Digite o Peso: ");
                                    String buscaPeso = scanner.nextLine().trim();
                                    filtrados = filtrados.stream()
                                            .filter(p -> p.peso.equals(buscaPeso))
                                            .collect(Collectors.toList());
                                    break;
                                case "5": // Raça (Busca por PARTES)
                                    System.out.print("Digite a Raça: ");
                                    String buscaRaca = normalizar(scanner.nextLine());
                                    filtrados = filtrados.stream()
                                            .filter(p -> normalizar(p.raca).contains(buscaRaca))
                                            .collect(Collectors.toList());
                                    break;
                                case "6": // Endereço (Busca por PARTES)
                                    System.out.print("Digite parte do Endereço: ");
                                    String buscaEnd = normalizar(scanner.nextLine());
                                    filtrados = filtrados.stream()
                                            .filter(p -> normalizar(p.rua + " " + p.bairro + " " + p.cidade).contains(buscaEnd))
                                            .collect(Collectors.toList());
                                    break;

                            }
                        }
                        System.out.println("\n --- RESULTADOS ENCONTRADOS ---");
                        if (filtrados.isEmpty()) {
                            System.out.println("Nenhum pet encontrado para os critérios informados.");
                        } else {
                            for (int i = 0; i < filtrados.size(); i++) {
                                pet p = filtrados.get(i);


                                // Regra de Formato: 1. Rex - Cachorro - Macho - Rua 1, 123 - Cidade 1 - 2 anos - 5kg - Vira-lata
                                System.out.printf("%d. %s - %s - %s - %s, %s - %s - %s anos - %skg - %s%n",
                                        (i + 1),
                                        p.nome,
                                        // Converte ENUM (CACHORRO) para texto amigável (Cachorro)
                                        p.tipo.name().substring(0, 1) + p.tipo.name().substring(1).toLowerCase(),
                                        p.sexo.name().substring(0, 1) + p.sexo.name().substring(1).toLowerCase(),
                                        p.rua, p.numero, p.cidade,
                                        p.idade,
                                        p.peso,
                                        p.raca);

                            }
                        }
                        imprimirMenu();

                        break;
                    case 6:
                        System.out.println("Sair");
                        break;
                }
                if (opcao <= 0 || opcao > 6) {
                    System.out.println("Opção inválida");
                    imprimirMenu();
                }
            } else {
                System.out.println("Erro: Você digitou uma letra ou símbolo. use apenas números.");
                scanner.next();
                imprimirMenu();
            }
        }
        scanner.close();
    }

    public static String normalizar(String texto) {
        if (texto == null) return "";
        String nfd = java.text.Normalizer.normalize(texto, Normalizer.Form.NFD);
        return nfd.replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase().trim();
    }

    public static void imprimirMenu() {
        System.out.println("---------SEJA BEM VINDO AO PET ONLINE---------");
        System.out.println("1.Cadastrar um novo pet");
        System.out.println("2.Alterar os dados do pet cadastrado");
        System.out.println("3.Deletar um pet cadastrado");
        System.out.println("4.Listar todos os pets cadastrados");
        System.out.println("5.Listar pets por algum critério (idade, nome, raça)");
        System.out.println("6.Sair");
        System.out.println("Escolha uma opção: ");
    }
}
