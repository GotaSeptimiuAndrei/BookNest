import { useQuery } from "@tanstack/react-query"
import { AuthorControllerService } from "@/api"
import type { AuthorResponse } from "@/api/generated"

export const useAuthor = (fullName: string | undefined) =>
    useQuery<AuthorResponse>({
        queryKey: ["author", fullName],
        enabled: !!fullName,
        queryFn: () => AuthorControllerService.getAuthorByFullName({ fullName: fullName! }).then((r) => r.results!),
    })
