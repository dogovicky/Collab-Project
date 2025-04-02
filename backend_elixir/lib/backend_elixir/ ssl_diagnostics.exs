defmodule BackendElixir.SSLDiagnostics do
  require Logger

  def diagnose_certificate(cacertfile_path \\ "/home/elon/cacert.pem") do
    IO.puts("Starting SSL Certificate Diagnostics")
    IO.puts("Checking certificate file: #{cacertfile_path}")

    # File existence check
    unless File.exists?(cacertfile_path) do
      IO.puts("ERROR: Certificate file does not exist!")
      return_diagnostic_result(:file_not_found)
    end

    # File read attempt
    case File.read(cacertfile_path) do
      {:ok, pem_content} ->
        IO.puts("Certificate file read successfully")
        IO.puts("File size: #{byte_size(pem_content)} bytes")

        # PEM decoding attempts
        try do
          pem_entries = :public_key.pem_decode(pem_content)
          IO.puts("Number of PEM entries found: #{length(pem_entries)}")

          Enum.with_index(pem_entries, fn entry, index ->
            try do
              decoded_entry = :public_key.pem_entry_decode(entry)
              IO.puts("Entry #{index + 1}: Successfully decoded")

              # Additional type checking
              case decoded_entry do
                {:Certificate, _, _} ->
                  IO.puts("Entry #{index + 1} is a valid X.509 Certificate")

                _ ->
                  IO.puts("Entry #{index + 1} is of type: #{inspect(elem(decoded_entry, 0))}")
              end
            catch
              type, reason ->
                IO.puts(
                  "Entry #{index + 1} decoding failed: #{inspect(type)} - #{inspect(reason)}"
                )
            end
          end)
        catch
          type, reason ->
            IO.puts("PEM decoding failed: #{inspect(type)} - #{inspect(reason)}")
        end

      {:error, reason} ->
        IO.puts("ERROR: Could not read certificate file")
        IO.puts("Reason: #{inspect(reason)}")
        return_diagnostic_result(:read_error)
    end

    # SSL Verification test
    IO.puts("\nAttempting SSL Verification Test:")
    verify_ssl_connection()
  end

  defp verify_ssl_connection do
    rabbitmq_url = System.get_env("RABBITMQ_URL")
    rabbitmq_host = System.get_env("RABBITMQ_HOST")

    cond do
      is_nil(rabbitmq_url) ->
        IO.puts("RABBITMQ_URL is not set!")
        return_diagnostic_result(:missing_url)

      is_nil(rabbitmq_host) ->
        IO.puts("RABBITMQ_HOST is not set!")
        return_diagnostic_result(:missing_host)

      true ->
        ssl_options = [
          verify: :verify_peer,
          cacertfile: "/home/elon/cacert.pem",
          server_name_indication: String.to_charlist(rabbitmq_host)
        ]

        try do
          case :ssl.connect(String.to_charlist(rabbitmq_host), 5671, ssl_options, 5000) do
            {:ok, _} ->
              IO.puts("SSL Connection test successful")

            {:error, reason} ->
              IO.puts("SSL Connection failed: #{inspect(reason)}")
          end
        catch
          type, reason ->
            IO.puts(
              "SSL Connection test threw an exception: #{inspect(type)} - #{inspect(reason)}"
            )
        end
    end
  end

  defp return_diagnostic_result(result) do
    IO.puts("\nDiagnostic Result: #{inspect(result)}")
    result
  end
end
