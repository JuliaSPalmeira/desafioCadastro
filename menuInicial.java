import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class pet {
    String nome, tipo, sexo, bairro, idade, peso, raca;

    public pet(String nome, String tipo, String sexo, String bairro, String idade, String peso, String raca) {
        this.nome = nome;
        this.tipo = tipo;
        this.sexo = sexo;
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
                            if (!nomeCompleto.contains(" ") || nomeCompleto.split(" ").length < 2) {
                                throw new IllegalArgumentException("Erro: Você deve digitar nome e sobrenome!");
                            }

                            System.out.print(linhas.get(1) + " ");
                            String tipo = scanner.nextLine();

                            System.out.print(linhas.get(2) + " ");
                            String sexo = scanner.nextLine();

                            System.out.print(linhas.get(3) + " ");
                            String bairro = scanner.nextLine();

                            System.out.print(linhas.get(4) + " ");
                            String idade = scanner.nextLine();

                            System.out.print(linhas.get(5) + " ");
                            String peso = scanner.nextLine();

                            System.out.print(linhas.get(6) + " ");
                            String raca = scanner.nextLine();

                            pet novopet = new pet(nomeCompleto, tipo, sexo, bairro, idade, peso, raca);
                            bancoDeDados.add(novopet);

                            System.out.println("\n✅ Pet cadastrado com sucesso!");
                            imprimirMenu();
                        } catch (IllegalArgumentException e) {
                            System.err.println(e.getMessage());
                            System.out.println("O cadastro foi cancelado. tente novamente");
                            imprimirMenu();
                        }
                        break;
                    case 2:
                        System.out.println("Alterar os dados do pet cadastrado");
                        break;
                    case 3:
                        System.out.println("Deletar os dados do pet cadastrado");
                        break;
                    case 4:
                        System.out.println("Listar todos os pets cadastrados");
                        break;
                    case 5:
                        System.out.println("Listar pets por algum critério (idade, nome, raça)");
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
