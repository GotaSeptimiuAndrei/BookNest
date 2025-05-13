import Container from "@mui/material/Container"
import CommunityForm from "../components/CommunityForm"
import { useAuth } from "@/context/AuthContext"
import { useCreateCommunity } from "../hooks/useCreateCommunity"
import { Navigate } from "react-router-dom"

export default function CreateCommunityPage() {
    const { user } = useAuth()
    const create = useCreateCommunity()

    if (!user || !user.roles.includes("AUTHOR")) return <Navigate to="/" />

    return (
        <Container sx={{ py: 4 }}>
            <CommunityForm authorId={user.id} onSubmit={(d) => create.mutateAsync(d)} loading={create.isPending} />
        </Container>
    )
}
